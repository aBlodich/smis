package com.ablodich.smis.diagnostictaskrouterservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTypeDto;
import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTypeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/diagnosis-types")
public class DiagnosisTypeController {

    private final DiagnosisTypeService diagnosisTypeService;

    @Operation(summary = "Получение списка всех типов проверок снимков")
    @GetMapping
    public ApiResponseDto<List<DiagnosisTypeDto>> getDiagnosisTypes() {
        var types = diagnosisTypeService.findAllTypes();
        return ApiResponseDto.ok(types);
    }
}
