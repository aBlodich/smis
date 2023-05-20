package com.ablodich.smis.appointmentservice.listener;

import com.ablodich.smis.common.event.AppointmentValidationEvent;
import com.ablodich.smis.appointmentservice.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentValidationEventTopicListener {
    private final AppointmentService appointmentService;

    @KafkaListener(topics = "${service.topics.appointment-validation-event-topic}")
    public void validateAppointment(AppointmentValidationEvent event) {
        log.debug("Пришло событие на валидацию записи к врачу: {}", event);
        appointmentService.validate(event);
    }
}
