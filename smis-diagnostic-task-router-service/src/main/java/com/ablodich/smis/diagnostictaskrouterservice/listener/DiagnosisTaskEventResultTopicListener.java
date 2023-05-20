package com.ablodich.smis.diagnostictaskrouterservice.listener;

import com.ablodich.smis.common.event.DiagnosisTaskEventResult;
import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisTaskEventResultTopicListener {
    private final DiagnosisTaskService diagnosisTaskService;
    @KafkaListener(topics = "${service.topics.diagnosis-task-event-result-topic}")
    public void listen(DiagnosisTaskEventResult diagnosisTaskEventResult) {
        log.debug("Пришел результат задачи: {}", diagnosisTaskEventResult);
        try {
            diagnosisTaskService.processDiagnosisTaskEventResult(diagnosisTaskEventResult);
        } catch (Exception e) {
            log.error("Ошибка во время обработки результата проверки снимка: ", e);
            diagnosisTaskService.finishTaskWithError(diagnosisTaskEventResult.getTaskId(), "Ошибка во время обработки результата проверки снимка: " + e.getMessage());
        }
    }
}
