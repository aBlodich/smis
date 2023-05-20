package com.ablodich.smis.diagnostictaskrouterservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.common.model.dto.IdResultDto;
import com.ablodich.smis.diagnostictaskrouterservice.dto.CreateTaskDto;
import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTaskDto;
import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTaskService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/tasks")
public class TaskController {

    private final DiagnosisTaskService diagnosisTaskService;

    @Operation(summary = "Создать задачу на анализ медицинского изображения")
    @PostMapping
    public ApiResponseDto<IdResultDto<UUID>> createTask(CreateTaskDto createTaskDto) {
        return ApiResponseDto.ok(new IdResultDto<>(diagnosisTaskService.createTask(createTaskDto.getAppointmentId().orElse(null), createTaskDto.getDiagnosisCode(), createTaskDto.getFileId())));
    }

    @Operation(summary = "Получить задачу на анализ медицинского изображения")
    @GetMapping("/{taskId}")
    public ApiResponseDto<DiagnosisTaskDto> getTask(@PathVariable UUID taskId) {
        return ApiResponseDto.ok(diagnosisTaskService.findByById(taskId));
    }
}
