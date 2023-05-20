package com.ablodich.smis.diagnostictaskrouterservice.dto;

import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisType;

import java.io.Serializable;

/**
 * DTO for {@link DiagnosisType}
 */
public record DiagnosisTypeDto(Integer id, String name, String code) implements Serializable {}