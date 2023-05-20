package com.ablodich.smis.diagnostictaskrouterservice.repository;

import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTask;
import com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

public interface DiagnosisTaskRepository extends JpaRepository<DiagnosisTask, UUID> {

    @Query("select d from DiagnosisTask d where d.state not in " +
            "(com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState.COMPLETED, "
            + "com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState.ERROR, "
            + "com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState.STARTED)")
    Set<DiagnosisTask> findIncompleteDiagnosisTasks();
}