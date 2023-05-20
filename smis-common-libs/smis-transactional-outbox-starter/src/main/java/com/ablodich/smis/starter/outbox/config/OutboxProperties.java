package com.ablodich.smis.starter.outbox.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smis.outbox")
public class OutboxProperties {
    private Integer maxTryCount;
    private OutboxSchedulingProperties scheduling;

    @Data
    private static class OutboxSchedulingProperties {
        private Long delayMs;
        private String lockAtMostFor;
        private String lockAtLeastFor;
    }
}
