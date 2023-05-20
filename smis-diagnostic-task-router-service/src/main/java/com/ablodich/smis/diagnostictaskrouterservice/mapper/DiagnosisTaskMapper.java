package com.ablodich.smis.diagnostictaskrouterservice.mapper;

import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTaskDto;
import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTaskResultDto;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTask;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTaskResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.CollectionUtils;

@Mapper(componentModel = "spring")
public interface DiagnosisTaskMapper {

    @Mapping(target = "result", expression = "java(getActiveResult(task))")
    DiagnosisTaskDto diagnosisTaskToDiagnosisTaskDto(DiagnosisTask task);

    default DiagnosisTaskResultDto getActiveResult(DiagnosisTask task) {
        if (CollectionUtils.isEmpty(task.getResults())) {
            return null;
        }

        DiagnosisTaskResult result = task.getResults().stream()
                                         .filter(DiagnosisTaskResult::getActive)
                                         .findFirst()
                                         .orElse(null);

        if (result == null) {
            return null;
        }

        return new DiagnosisTaskResultDto(result.getId(), result.getPrediction(), result.getSegmentedFileId());
    }
}
