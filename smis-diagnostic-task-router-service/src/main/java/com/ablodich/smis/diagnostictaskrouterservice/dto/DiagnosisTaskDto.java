package com.ablodich.smis.diagnostictaskrouterservice.dto;

import com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTask}
 */
public record DiagnosisTaskDto(UUID id,
                               LocalDateTime createdAt,
                               String errorDescription,
                               UUID appointmentId,
                               String originalFileId,
                               DiagnosisTaskState state,
                               DiagnosisTypeDto diagnosisType,
                               DiagnosisTaskResultDto result) implements Serializable {}