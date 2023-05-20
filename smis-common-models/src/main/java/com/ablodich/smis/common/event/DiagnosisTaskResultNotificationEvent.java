package com.ablodich.smis.common.event;

import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventState;
import lombok.Data;

import java.util.UUID;

@Data
public class DiagnosisTaskResultNotificationEvent {
    private UUID id;
    private DiagnosisTaskResultInfo diagnosisTaskResultInfo;
    private DiagnosisTaskEventState state;
    private String errorDescription;
}
