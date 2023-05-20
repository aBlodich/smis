package com.ablodich.smis.starter.outbox.service;

import com.ablodich.smis.starter.outbox.exception.OutboxException;
import com.ablodich.smis.starter.outbox.model.Outbox;
import com.ablodich.smis.starter.outbox.model.OutboxStatus;
import com.ablodich.smis.starter.outbox.repository.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional(propagation = Propagation.MANDATORY)
    public <T> void sendMessage(String topic, T payload) throws JsonProcessingException {
        sendMessage(null, topic, payload);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public <T> void sendMessage(String messageKey, String topic, T payload) {
        String jsonPayload = null;
        try {
            jsonPayload = objectMapper.writeValueAsString(payload);
            Outbox outbox = new Outbox();
            outbox.setId(UUID.randomUUID());
            outbox.setTopic(topic);
            outbox.setMessageKey(messageKey);
            outbox.setPayload(jsonPayload);
            outbox.setCreatedAt(Instant.now().atOffset(ZoneOffset.UTC).toLocalDateTime());
            outbox.setStatus(OutboxStatus.NEW);
            outboxRepository.saveOutbox(outbox);
        }
        catch (JsonProcessingException e) {
            log.error("Ошибка при сериализации сообщения полезной нагрузки для outbox:\n", e);
            throw new OutboxException(e);
        }
    }

    @Transactional(readOnly = true)
    public List<Outbox> findAllUnprocessedMessages() {
        try {
            return outboxRepository.findAllUnprocessedOutboxes();
        } catch (Exception e) {
            log.error("Ошибка во время поиска необработанных сообщений в таблице Outbox:\n", e);
        }
        return Collections.emptyList();
    }

    @Transactional
    public void removeMessagesByIds(final List<UUID> successIds) {
        try {
            int removedRows = outboxRepository.removeByIds(successIds);
            if (removedRows != successIds.size()) {
                log.error("Не все сообщения были удалены из таблицы outbox. Количество удаленных: {}, количество для удаления: {}",
                          removedRows,
                          successIds.size());
            }
        } catch (Exception e) {
            log.error("Ошибка во время удаления сообщений из таблицы Outbox:\n", e);
        }
    }

    @Transactional
    public void setSendErrorForMessages(final Map<Outbox, String> failedMessages) {
        for (var entry : failedMessages.entrySet()) {
            setSendErrorForMessage(entry);
        }
    }

    public void setSendErrorForMessage(final Map.Entry<Outbox, String> entry) {
        try {
            Outbox outbox = entry.getKey();
            String errorMessage = entry.getValue();
            int updateCount = outboxRepository.setSendErrorForMessage(outbox.getId(), errorMessage);
            if (updateCount != 1) {
                log.error("Не удалось обновить запись в таблице outbox");
            }
        } catch (Exception e) {
            log.error("Ошибка во время установки ошибочного состояния для записи в таблице outbox: ", e);
        }
    }
}
