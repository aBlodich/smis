package com.ablodich.smis.appointmentservice.listener;

import com.ablodich.smis.appointmentservice.service.AppointmentInfoService;
import com.ablodich.smis.common.event.LinkAppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAppointmentEventTopicListener {
    private final AppointmentInfoService appointmentInfoService;

    @KafkaListener(topics = "${service.topics.link-appointment-event-topic}")
    public void process(LinkAppointmentEvent event) {
        log.debug("Получили событие связывания файла с записью к врачу: {}", event);
        try {
            appointmentInfoService.createAttachmentForAppointmentInfo(event);
        } catch (Exception e) {
            appointmentInfoService.processUnexpectedExceptionOnCreatingAttachment(event, e);
        }
    }
}
