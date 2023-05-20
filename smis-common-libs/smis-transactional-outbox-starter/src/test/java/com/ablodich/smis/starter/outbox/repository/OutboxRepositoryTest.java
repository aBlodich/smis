package com.ablodich.smis.starter.outbox.repository;

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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Transactional
@Testcontainers
@Sql(scripts = "classpath:/db/initial.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@SpringBootTest(classes = SmisOutboxAutoConfiguration.class)
@EnableAutoConfiguration
class OutboxRepositoryTest {
    @Autowired
    private OutboxRepository outboxRepository;
    @Autowired
    @Qualifier("smisObjectMapper")
    private ObjectMapper objectMapper;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    void saveOutbox() throws JsonProcessingException {
        Outbox outbox = prepareOutbox();
        outboxRepository.saveOutbox(outbox);
        Outbox savedOutbox = namedParameterJdbcTemplate.queryForObject("select * from outbox;", Collections.emptyMap(), outboxRepository::mapResultSetToOutbox);
        log.info("{}", savedOutbox);
        assertThat(savedOutbox).isNotNull();
        assertThat(savedOutbox.getPayload()).isEqualTo(outbox.getPayload());
        assertThat(savedOutbox.getTopic()).isEqualTo(outbox.getTopic());
        assertThat(savedOutbox.getMessageKey()).isEqualTo(outbox.getMessageKey());
        assertThat(savedOutbox.getCreatedAt()).isEqualTo(outbox.getCreatedAt());
        assertThat(savedOutbox.getStatus()).isEqualTo(outbox.getStatus());
    }

    @Test
    void findAllUnprocessedOutboxes() throws JsonProcessingException {
        Outbox outbox = prepareOutbox();
        outboxRepository.saveOutbox(outbox);
        outbox.setId(UUID.randomUUID());
        outboxRepository.saveOutbox(outbox);
        outbox.setId(UUID.randomUUID());
        outboxRepository.saveOutbox(outbox);
        List<Outbox> outboxes = outboxRepository.findAllUnprocessedOutboxes();
        assertThat(outboxes).hasSize(3);

    }

    @Test
    void setSendErrorForMessage() throws JsonProcessingException {
        Outbox outbox = prepareOutbox();
        outboxRepository.saveOutbox(outbox);
        outbox.setId(UUID.randomUUID());
        outboxRepository.saveOutbox(outbox);
        outbox.setId(UUID.randomUUID());
        outboxRepository.saveOutbox(outbox);

        for (int i = 0; i < 11; i++) {
            outboxRepository.setSendErrorForMessage(outbox.getId(), "error");
        }

        List<Outbox> outboxes = outboxRepository.findAllUnprocessedOutboxes();
        assertThat(outboxes).hasSize(2);
    }

    @Test
    void removeByIds() throws JsonProcessingException {
        Outbox outbox1 = prepareOutbox();
        Outbox outbox2 = prepareOutbox();
        Outbox outbox3 = prepareOutbox();
        outboxRepository.saveOutbox(outbox1);
        outboxRepository.saveOutbox(outbox2);
        outboxRepository.saveOutbox(outbox3);
        List<UUID> ids = new LinkedList<>();
        ids.add(outbox1.getId());
        ids.add(outbox2.getId());
        ids.add(outbox3.getId());
        int removesCount = outboxRepository.removeByIds(ids);
        assertThat(removesCount).isEqualTo(3);
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

    private Outbox prepareOutbox() throws JsonProcessingException {
        TestClass payload = prepareTestObject("test", LocalDateTime.now());
        String jsonPayload = objectMapper.writeValueAsString(payload);
        Outbox outbox = new Outbox();
        outbox.setId(UUID.randomUUID());
        outbox.setTopic("topic");
        outbox.setMessageKey("key");
        outbox.setPayload(jsonPayload);
        outbox.setCreatedAt(Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime());
        outbox.setStatus(OutboxStatus.NEW);
        return outbox;
    }

    private TestClass prepareTestObject(String s, LocalDateTime i) {
        return new TestClass(s, i);
    }
}
