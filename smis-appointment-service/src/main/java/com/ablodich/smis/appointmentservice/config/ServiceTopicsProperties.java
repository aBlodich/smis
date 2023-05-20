package com.ablodich.smis.appointmentservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "service.topics")
public class ServiceTopicsProperties {
    private String appointmentValidationResultTopic;
    private String linkAppointmentResultTopic;
}
