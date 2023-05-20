package com.ablodich.smis.starter.outbox.config;

import com.ablodich.smis.starter.outbox.repository.OutboxRepository;
import com.ablodich.smis.starter.outbox.service.MessageRelayService;
import com.ablodich.smis.starter.outbox.service.OutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;


@Configuration
@ConditionalOnClass({DataSource.class, KafkaTemplate.class})
@AutoConfigureBefore(KafkaAutoConfiguration.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
@EnableSchedulerLock(defaultLockAtMostFor = "1m")
@Import(KafkaConfiguration.class)
public class SmisOutboxAutoConfiguration {

    @Bean("smisOutboxJdbcTemplate")
    public NamedParameterJdbcTemplate smisOutboxJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public OutboxRepository outboxRepository(@Qualifier("smisOutboxJdbcTemplate") NamedParameterJdbcTemplate smisOutboxJdbcTemplate,
                                             OutboxProperties outboxProperties) {
        return new OutboxRepository(smisOutboxJdbcTemplate, outboxProperties);
    }

    @Bean
    public OutboxService outboxService(OutboxRepository outboxRepository,
                                       @Qualifier("smisObjectMapper") ObjectMapper smisObjectMapper) {
        return new OutboxService(outboxRepository, smisObjectMapper);
    }

    @Bean
    public MessageRelayService messageRelayService(OutboxService outboxService,
                                                   KafkaTemplate<String, String> kafkaTemplate,
                                                   OutboxProperties outboxProperties) {
        return new MessageRelayService(outboxService, kafkaTemplate, outboxProperties);
    }

    @Bean
    public LockProvider lockProvider(final DataSource dataSource) {
        return new JdbcTemplateLockProvider(dataSource);
    }

    @Bean
    public JsonMessageConverter jsonMessageConverter(@Qualifier("smisObjectMapper") ObjectMapper smisObjectMapper) {
        return new StringJsonMessageConverter(smisObjectMapper);
    }
}
