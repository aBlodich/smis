package com.ablodich.smis.diagnostictaskrouterservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "service.topics")
public class ServiceTopicsProperties {
    private String appointmentValidationEventTopic;
    private String linkAppointmentEventTopic;
    private String taskResultNotificationTopic;
    private Map<String, String> diagnosisServicesTasks;
}
