package com.ablodich.smis.diagnostictaskrouterservice.service;

import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTypeDto;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisType;
import com.ablodich.smis.diagnostictaskrouterservice.mapper.DiagnosisTypeMapper;
import com.ablodich.smis.diagnostictaskrouterservice.repository.DiagnosisTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.ablodich.smis.diagnostictaskrouterservice.constants.Constants.DIAGNOSIS_TYPE_NOT_FOUND;
import static com.ablodich.smis.diagnostictaskrouterservice.constants.Constants.DIAGNOSIS_TYPE_NOT_FOUND_BY_CODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisTypeService {
    private final DiagnosisTypeRepository diagnosisTypeRepository;
    private final DiagnosisTypeMapper diagnosisTypeMapper;

    @Transactional(readOnly = true)
    public DiagnosisType findDiagnosisTypeEntity(int id) {
        return diagnosisTypeRepository.findById(id)
                                      .orElseThrow(() -> new NotFoundException(DIAGNOSIS_TYPE_NOT_FOUND + id));
    }

    @Transactional(readOnly = true)
    public DiagnosisType findDiagnosisTypeEntityByCode(final String diagnosisCode) {
        return diagnosisTypeRepository.findByCode(diagnosisCode)
                                      .orElseThrow(() -> new NotFoundException(DIAGNOSIS_TYPE_NOT_FOUND_BY_CODE + diagnosisCode));
    }

    @Transactional(readOnly = true)
    public List<DiagnosisTypeDto> findAllTypes() {
        return diagnosisTypeRepository.findAll().stream().map(diagnosisTypeMapper::diagnosisTypeToDiagnosisTypeDto).collect(Collectors.toList());
    }
}
