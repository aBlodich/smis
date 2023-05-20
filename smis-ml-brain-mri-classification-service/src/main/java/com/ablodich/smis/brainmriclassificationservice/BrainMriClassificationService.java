package com.ablodich.smis.brainmriclassificationservice;

import com.ablodich.smis.brainmriclassificationservice.config.ServiceTopicsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(ServiceTopicsProperties.class)
public class BrainMriClassificationService {

    public static void main(String[] args) {
        SpringApplication.run(BrainMriClassificationService.class, args);
    }
}
