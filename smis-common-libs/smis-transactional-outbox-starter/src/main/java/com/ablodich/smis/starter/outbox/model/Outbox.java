package com.ablodich.smis.starter.outbox.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
public class Outbox {
    private UUID id;
    private String messageKey;
    private String topic;
    private String payload;
    private OutboxStatus status;
    private LocalDateTime createdAt;
    private Integer tryCount;
    private LocalDateTime lastTryAt;
    private String errorDescription;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Outbox outbox = (Outbox) o;

        return id.equals(outbox.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
