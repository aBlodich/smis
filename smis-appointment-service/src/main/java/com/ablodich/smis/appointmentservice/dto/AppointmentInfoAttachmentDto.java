package com.ablodich.smis.appointmentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentInfoAttachmentDto {
    private UUID id;
    private String originalFileId;
    private String segmentedFileId;
}
