package com.ablodich.smis.appointmentservice.service;


import com.ablodich.smis.appointmentservice.dto.appointmentinfo.AppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PostAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PutAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.entity.AppointmentInfo;
import com.ablodich.smis.appointmentservice.repository.AppointmentInfoRepository;
import com.ablodich.smis.common.event.LinkAppointmentEvent;

import java.util.UUID;

public interface AppointmentInfoService {
    AppointmentInfo findAppointmentInfoEntityById(UUID id);
    AppointmentInfoDto findAppointmentInfoById(UUID id);
    AppointmentInfoDto createAppointmentInfo(UUID appointmentId, PostAppointmentInfoDto appointmentInfoDto);

    AppointmentInfoDto updateAppointmentInfo(PutAppointmentInfoDto appointmentInfoDto);

    AppointmentInfoDto findAppointmentInfoByAppointmentId(UUID appointmentId);

    void createAttachmentForAppointmentInfo(LinkAppointmentEvent event);

    void processUnexpectedExceptionOnCreatingAttachment(LinkAppointmentEvent event, Exception e);

    AppointmentInfoRepository getRepository();
}
