package com.ablodich.smis.brainmriclassificationservice.listener;

import com.ablodich.smis.brainmriclassificationservice.service.ClassificationService;
import com.ablodich.smis.common.event.DiagnosisTaskEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmisBrainMriTumorClassificationTopicListener {
    private final ClassificationService classificationService;

    @KafkaListener(topics = "${service.topics.smis-brain-mri-tumor-classification-topic}")
    public void process(DiagnosisTaskEvent diagnosisTaskEvent) {
        log.debug("Пришла задача на классификацию: {}", diagnosisTaskEvent);
        classificationService.classify(diagnosisTaskEvent);
    }
}
