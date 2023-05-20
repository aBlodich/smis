package com.ablodich.smis.starter.outbox.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private static final int DEFAULT_SEND_TIMEOUT = 60000;

    private final KafkaProperties kafkaProperties;

    @Bean("outboxProducerFactory")
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> producerProperties = this.kafkaProperties.buildProducerProperties();
        Map<String, Object> defaultProducerProperties = new HashMap<>(producerProperties);
        defaultProducerProperties.put(ProducerConfig.ACKS_CONFIG, "all");
        defaultProducerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        defaultProducerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        defaultProducerProperties.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, DEFAULT_SEND_TIMEOUT);
        return new DefaultKafkaProducerFactory<>(defaultProducerProperties);
    }

    @Bean("outboxKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate(@Qualifier("outboxProducerFactory") ProducerFactory<String, String> producerFactory,
                                                       @Qualifier("outboxKafkaProducerListener") ProducerListener<String, String> kafkaProducerListener,
                                                       ObjectProvider<RecordMessageConverter> messageConverter) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        messageConverter.ifUnique(kafkaTemplate::setMessageConverter);
        kafkaTemplate.setProducerListener(kafkaProducerListener);
        return kafkaTemplate;
    }

    @Bean("outboxKafkaProducerListener")
    public ProducerListener<String, String> kafkaProducerListener() {
        LoggingProducerListener<String, String> producerListener = new LoggingProducerListener<>();
        producerListener.setIncludeContents(false);
        return producerListener;
    }

    @Bean("smisObjectMapper")
    public ObjectMapper smisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
