package com.ablodich.smis.appointmentservice.dto.appointment;

import com.ablodich.smis.appointmentservice.entity.enumerate.AppointmentFormat;
import com.ablodich.smis.appointmentservice.entity.enumerate.AppointmentType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record PostAppointmentDto(UUID patientId,
                                 UUID doctorId,
                                 Integer doctorSpecializationId,
                                 LocalDateTime appointmentDate,
                                 AppointmentType type,
                                 AppointmentFormat format) implements Serializable {

}
