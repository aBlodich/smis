package com.ablodich.smis.diagnostictaskrouterservice.listener;

import com.ablodich.smis.common.event.AppointmentValidationEventResult;
import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentValidationResultTopicListener {
    private final DiagnosisTaskService diagnosisTaskService;
    @KafkaListener(topics = "${service.topics.appointment-validation-result-topic}")
    public void listen(AppointmentValidationEventResult appointmentValidationEventResult) {
        log.debug("Пришел результат валидации записи: {}", appointmentValidationEventResult);
        diagnosisTaskService.processAppointmentValidation(appointmentValidationEventResult);
    }
}
