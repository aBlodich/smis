package com.ablodich.smis.diagnostictaskrouterservice.mapper;

import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTypeDto;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiagnosisTypeMapper {
    DiagnosisTypeDto diagnosisTypeToDiagnosisTypeDto(DiagnosisType type);
}
