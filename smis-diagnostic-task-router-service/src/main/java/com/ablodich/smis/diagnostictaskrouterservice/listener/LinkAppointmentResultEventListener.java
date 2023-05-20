package com.ablodich.smis.diagnostictaskrouterservice.listener;

import com.ablodich.smis.common.event.LinkAppointmentResultEvent;
import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAppointmentResultEventListener {
    private final DiagnosisTaskService diagnosisTaskService;

    @KafkaListener(topics = "${service.topics.link-appointment-result-topic}")
    public void process(LinkAppointmentResultEvent event) {
        log.debug("Пришел результат связывания файла с записью к врачу: {}", event);
        diagnosisTaskService.processLinkAppointmentEvent(event);
    }
}
