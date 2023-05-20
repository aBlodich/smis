package com.ablodich.smis.diagnostictaskrouterservice;

import com.ablodich.smis.diagnostictaskrouterservice.config.ServiceTopicsProperties;
import com.ablodich.smis.starter.outbox.config.SmisOutboxAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(ServiceTopicsProperties.class)
public class DiagnosticTaskRouterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiagnosticTaskRouterServiceApplication.class, args);
    }
}
