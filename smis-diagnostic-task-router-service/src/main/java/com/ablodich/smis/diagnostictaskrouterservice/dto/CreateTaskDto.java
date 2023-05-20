package com.ablodich.smis.diagnostictaskrouterservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

@Data
public class CreateTaskDto {
    private final Optional<UUID> appointmentId;
    @NotNull
    private String diagnosisCode;
    @NotNull
    private String fileId;
}
