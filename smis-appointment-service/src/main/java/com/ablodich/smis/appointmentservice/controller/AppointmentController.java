package com.ablodich.smis.appointmentservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.appointmentservice.dto.appointment.AppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PostAppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PutAppointmentDto;
import com.ablodich.smis.appointmentservice.service.AppointmentService;
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
@RequestMapping("/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentService appointmentService;

    @Operation(summary = "Получение записи на прием к врачу по ее id")
    @GetMapping("/{appointmentId}")
    public ApiResponseDto<AppointmentDto> getAppointment(@PathVariable final UUID appointmentId) {
        var appointment = appointmentService.findAppointmentById(appointmentId);
        return ApiResponseDto.ok(appointment);
    }

    @Operation(summary = "Создание записи на прием к врачу")
    @PostMapping
    public ApiResponseDto<AppointmentDto> createAppointment(@RequestBody final PostAppointmentDto body) {
        var appointment = appointmentService.createAppointment(body);
        return ApiResponseDto.ok(appointment);
    }

    @Operation(summary = "Обновление записи на прием к врачу")
    @PutMapping
    public ApiResponseDto<AppointmentDto> updateAppointment(@RequestBody final PutAppointmentDto body) {
        var appointment = appointmentService.updateAppointment(body);
        return ApiResponseDto.ok(appointment);
    }
}
