package com.ablodich.smis.appointmentservice.repository;

import com.ablodich.smis.appointmentservice.entity.AppointmentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentInfoRepository extends JpaRepository<AppointmentInfo, UUID> {

    Optional<AppointmentInfo> findByAppointmentId(@NonNull UUID appointmentId);

}
