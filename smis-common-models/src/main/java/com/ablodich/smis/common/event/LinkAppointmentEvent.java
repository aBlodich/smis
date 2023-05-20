package com.ablodich.smis.common.event;

import lombok.Data;

import java.util.UUID;

@Data
public class LinkAppointmentEvent {
    private UUID taskId;
    private UUID appointmentId;
    private String originalFileId;
    private String segmentedFileId;
}
