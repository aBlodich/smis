package com.ablodich.smis.common.event;

import lombok.Data;

import java.util.UUID;

@Data
public class LinkAppointmentResultEvent {
    private UUID taskId;
    private boolean result;
    private String errorDescription;
}
