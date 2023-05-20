package com.ablodich.smis.diagnostictaskrouterservice.repository;

import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiagnosisTypeRepository extends JpaRepository<DiagnosisType, Integer> {

    Optional<DiagnosisType> findByCode(String diagnosisCode);
}