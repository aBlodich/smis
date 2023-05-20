package com.ablodich.smis.appointmentservice.dto.appointmentinfo;

import com.ablodich.smis.appointmentservice.dto.AppointmentInfoAttachmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.AppointmentDto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;


public record AppointmentInfoDto(UUID id,
                                 String inspection,
                                 String diagnose,
                                 String recommendations,
                                 AppointmentDto appointment,
                                 List<AppointmentInfoAttachmentDto> attachments) implements Serializable {

}