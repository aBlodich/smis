package com.ablodich.smis.appointmentservice.service;

import com.ablodich.smis.common.event.AppointmentValidationEvent;
import com.ablodich.smis.appointmentservice.dto.appointment.AppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PostAppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PutAppointmentDto;
import com.ablodich.smis.appointmentservice.entity.Appointment;
import com.ablodich.smis.appointmentservice.repository.AppointmentRepository;

import java.util.UUID;

public interface AppointmentService {
    Appointment findAppointmentEntityById(UUID id);
    AppointmentDto findAppointmentById(UUID id);

    AppointmentDto createAppointment(PostAppointmentDto appointmentDto);

    AppointmentDto updateAppointment(PutAppointmentDto appointmentDto);

    void validate(AppointmentValidationEvent event);

    AppointmentRepository getRepository();
}
