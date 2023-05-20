package com.ablodich.smis.diagnostictaskrouterservice.repository;

import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTaskResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;
import java.util.UUID;

public interface DiagnosisTaskResultRepository extends JpaRepository<DiagnosisTaskResult, UUID> {

    Set<DiagnosisTaskResult> findByDiagnosisTask_Id(UUID id);}