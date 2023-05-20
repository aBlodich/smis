package com.ablodich.smis.starter.outbox.service;

import com.ablodich.smis.starter.outbox.config.OutboxProperties;
import com.ablodich.smis.starter.outbox.model.Outbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RequiredArgsConstructor
public class MessageRelayService {
    private final OutboxService outboxService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxProperties outboxProperties;

    @SchedulerLock(name = "pollOutbox",
                   lockAtLeastFor = "${smis.outbox.scheduling.lockAtLeastFor:5000ms}",
                   lockAtMostFor = "${smis.outbox.scheduling.lockAtMostFor:10000ms}")
    @Scheduled(fixedDelayString = "${smis.outbox.scheduling.delayMs:500}")
    public void pollOutboxScheduled() {
        pollOutbox();
    }

    protected void pollOutbox() {
        log.debug("Начинаем опрос таблицы outbox");
        List<Outbox> messagesToSend = outboxService.findAllUnprocessedMessages();
        if (messagesToSend.isEmpty()) {
            return;
        }
        Map<Outbox, CompletableFuture<SendResult<String, String>>> successMessages = new LinkedHashMap<>();
        Map<Outbox, String> failedMessages = new LinkedHashMap<>();
        messagesToSend.forEach(m -> {
            try {
                var sendResult = relay(m);
                successMessages.put(m, sendResult);
            } catch (Exception e) {
                failedMessages.put(m, e.getMessage());
            }
        });
        processSuccessMessages(successMessages);
        processFailedMessages(failedMessages);
    }

    private CompletableFuture<SendResult<String, String>> relay(final Outbox m) {
        return kafkaTemplate.send(m.getTopic(), m.getMessageKey(), m.getPayload());
    }

    private void processSuccessMessages(final Map<Outbox, CompletableFuture<SendResult<String, String>>> successMessages) {
        List<UUID> successIds = new LinkedList<>();
        try {
            for (var e : successMessages.entrySet()) {
                e.getValue().get();
                successIds.add(e.getKey().getId());
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("Ошибка при выполнении отправки сообщения в kafka");
            throw new RuntimeException(e);
        }
        outboxService.removeMessagesByIds(successIds);
    }

    private void processFailedMessages(final Map<Outbox, String> failedMessages) {
        outboxService.setSendErrorForMessages(failedMessages);
    }
}
