package com.ablodich.smis.appointmentservice.mapper;

import com.ablodich.smis.appointmentservice.dto.AppointmentInfoAttachmentDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.AppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PostAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PutAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.entity.AppointmentInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppointmentInfoMapper {
    @Mapping(target = "id", source = "appointmentId")
    @Mapping(target = "attachments", expression = "java(getAttachments(appointmentInfo))")
    AppointmentInfoDto appointmentInfoToAppointmentInfoDto(AppointmentInfo appointmentInfo);
    AppointmentInfo postAppointmentInfoDtoToAppointmentInfo(PostAppointmentInfoDto appointmentInfoDto);
    AppointmentInfo updateAppointmentInfoFromPutAppointmentInfoDto(@MappingTarget AppointmentInfo appointmentInfo, PutAppointmentInfoDto appointmentInfoDto);

    default List<AppointmentInfoAttachmentDto> getAttachments(AppointmentInfo appointmentInfo) {
        return appointmentInfo.getAttachments().stream()
                              .map(a -> new AppointmentInfoAttachmentDto(a.getId().getAppointmentInfo().getAppointmentId(),
                                                                         a.getOriginalFileId(),
                                                                         a.getSegmentedFileId()))
                              .toList();
    }
}
