package com.ablodich.smis.brainmriclassificationservice.repository;

import com.ablodich.smis.brainmriclassificationservice.entity.MlModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MlModelRepository extends JpaRepository<MlModel, Integer> {

    @Query("select model from MlModel model where model.active = true")
    Optional<MlModel> findActive();
}