package com.ablodich.smis.appointmentservice.mapper;

import com.ablodich.smis.appointmentservice.dto.appointment.AppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PostAppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PutAppointmentDto;
import com.ablodich.smis.appointmentservice.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentDto appointmentToAppointmentDto(Appointment appointment);
    Appointment appointmentDtoToAppointment(AppointmentDto appointmentDto);
    Appointment postAppointmentDtoToAppointment(PostAppointmentDto appointmentDto);

    @Mapping(target = "id", ignore = true)
    void updateAppointmentFromPutAppointmentDto(@MappingTarget Appointment appointment, PutAppointmentDto appointmentDto);
}
