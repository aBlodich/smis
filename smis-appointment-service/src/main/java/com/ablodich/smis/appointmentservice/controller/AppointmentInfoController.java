package com.ablodich.smis.appointmentservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.AppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PostAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PutAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.service.AppointmentInfoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/appointments-infos")
@RequiredArgsConstructor
public class AppointmentInfoController {
    private final AppointmentInfoService appointmentInfoService;

    @Operation(summary = "Получение информации о приеме к врачу")
    @GetMapping("/{appointmentInfoId}")
    public ApiResponseDto<AppointmentInfoDto> getAppointmentInfo(@PathVariable final UUID appointmentInfoId) {
        var appointmentInfo = appointmentInfoService.findAppointmentInfoById(appointmentInfoId);
        return ApiResponseDto.ok(appointmentInfo);
    }

    @Operation(summary = "Создание информации о приеме к врачу")
    @PostMapping("/{appointmentId}")
    public ApiResponseDto<AppointmentInfoDto> createAppointmentInfo(@PathVariable final UUID appointmentId, @RequestBody final PostAppointmentInfoDto body) {
        var appointmentInfo = appointmentInfoService.createAppointmentInfo(appointmentId, body);
        return ApiResponseDto.ok(appointmentInfo);
    }

    @Operation(summary = "Обновление информации о приеме к врачу")
    @PutMapping
    public ApiResponseDto<AppointmentInfoDto> updateAppointmentInfo(@RequestBody final PutAppointmentInfoDto body) {
        var appointmentInfo = appointmentInfoService.updateAppointmentInfo(body);
        return ApiResponseDto.ok(appointmentInfo);
    }
}
