package com.ablodich.smis.common.event;

import com.ablodich.smis.common.event.enumerate.PredictionResult;

public record DiagnosisTaskResultInfo(PredictionResult prediction,
                                      String segmentedFileId) {}
