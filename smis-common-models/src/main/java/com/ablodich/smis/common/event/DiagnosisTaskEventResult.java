package com.ablodich.smis.common.event;

import com.ablodich.smis.common.event.enumerate.PredictionResult;
import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventResultState;
import lombok.Data;

import java.util.UUID;

@Data
public class DiagnosisTaskEventResult {
    private UUID taskId;
    private DiagnosisTaskEventResultState state;
    private PredictionResult prediction;
    private String segmentedFileId;
    private String errorDescription;
}
