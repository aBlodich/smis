package com.ablodich.smis.common.event;

import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventState;
import lombok.Data;

import java.util.UUID;

@Data
public class DiagnosisTaskEvent {
    private UUID id;
    private String fileId;
    private DiagnosisTaskEventState state;
}
