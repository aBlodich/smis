package com.ablodich.smis.diagnostictaskrouterservice.repository;

import com.ablodich.smis.diagnostictaskrouterservice.entity.CheckingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface CheckingServiceRepository extends JpaRepository<CheckingService, Long> {

    List<CheckingService> findByDiagnosisType_Code(@NonNull String code);}