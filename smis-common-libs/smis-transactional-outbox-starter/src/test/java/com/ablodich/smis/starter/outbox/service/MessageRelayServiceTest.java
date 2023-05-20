package com.ablodich.smis.starter.outbox.service;

import com.ablodich.smis.starter.outbox.config.SmisOutboxAutoConfiguration;
import com.ablodich.smis.starter.outbox.model.Outbox;
import com.ablodich.smis.starter.outbox.model.OutboxStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@Testcontainers
@Sql(scripts = "classpath:/db/initial.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = SmisOutboxAutoConfiguration.class)
@EnableAutoConfiguration
class MessageRelayServiceTest {
    @Autowired
    private MessageRelayService messageRelayService;
    @Autowired
    private OutboxService outboxService;
    @Autowired
    @Qualifier("smisObjectMapper")
    private ObjectMapper objectMapper;

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
            .withEnv("KAFKA_CREATE_TOPICS", "topic");

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        log.info("kafka container bootstrap-servers: {}", kafkaContainer.getBootstrapServers());
        registry.add("spring.kafka.properties.bootstrap.servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void pollOutbox_success() throws JsonProcessingException {
        TestClass payload = prepareTestObject("test", LocalDateTime.now());
        TestClass payload2 = prepareTestObject("test2", LocalDateTime.now());
        outboxService.sendMessage("topic", payload);
        outboxService.sendMessage("topic", payload2);

        List<Outbox> outboxesBeforePolling = outboxService.findAllUnprocessedMessages();
        assertThat(outboxesBeforePolling).hasSize(2);

        messageRelayService.pollOutbox();

        List<Outbox> outboxesAfterPolling = outboxService.findAllUnprocessedMessages();
        assertThat(outboxesAfterPolling).isEmpty();
    }

    @Test
    void pollOutbox_withLargeMessage_setErrorSentStatus() throws JsonProcessingException {
        char[] largeCharArray = new char[1024 * 1024];
        Arrays.fill(largeCharArray, 'a');
        String largeTestMessage = new String(largeCharArray);
        TestClass payload = prepareTestObject(largeTestMessage, LocalDateTime.now());
        TestClass payload2 = prepareTestObject("test2", LocalDateTime.now());
        outboxService.sendMessage("topic", payload);
        outboxService.sendMessage("topic", payload2);

        List<Outbox> outboxesBeforePolling = outboxService.findAllUnprocessedMessages();
        assertThat(outboxesBeforePolling).hasSize(2);

        messageRelayService.pollOutbox();

        List<Outbox> outboxesAfterPolling = outboxService.findAllUnprocessedMessages();
        assertThat(outboxesAfterPolling).hasSize(1);
        assertThat(outboxesAfterPolling.get(0).getPayload()).isEqualTo(objectMapper.writeValueAsString(payload));
        assertThat(outboxesAfterPolling.get(0).getStatus()).isEqualTo(OutboxStatus.SENT_ERROR);
        assertThat(outboxesAfterPolling.get(0).getLastTryAt()).isNotNull();
    }

    @Data
    public static class TestClass {
        private String testString;
        private LocalDateTime testDateTime;

        public TestClass(String testString,
                         LocalDateTime testDateTime) {
            this.testString = testString;
            this.testDateTime = testDateTime;
        }
    }

    private TestClass prepareTestObject(String s, LocalDateTime i) {
        return new TestClass(s, i);
    }
}
