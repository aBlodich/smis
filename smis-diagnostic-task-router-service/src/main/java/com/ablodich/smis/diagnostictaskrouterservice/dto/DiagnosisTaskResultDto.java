package com.ablodich.smis.diagnostictaskrouterservice.dto;

import com.ablodich.smis.common.event.enumerate.PredictionResult;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTaskResult;

import java.io.Serializable;
import java.util.UUID;

/**
 * DTO for {@link DiagnosisTaskResult}
 */
public record DiagnosisTaskResultDto(UUID id, PredictionResult prediction, String segmentedFileId) implements Serializable {}