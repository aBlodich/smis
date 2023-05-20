package com.ablodich.smis.appointmentservice.service;

import com.ablodich.smis.common.event.AppointmentValidationEvent;
import com.ablodich.smis.common.event.AppointmentValidationEventResult;
import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.appointmentservice.config.ServiceTopicsProperties;
import com.ablodich.smis.appointmentservice.constants.StringConstants;
import com.ablodich.smis.appointmentservice.dto.appointment.AppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PostAppointmentDto;
import com.ablodich.smis.appointmentservice.dto.appointment.PutAppointmentDto;
import com.ablodich.smis.appointmentservice.entity.Appointment;
import com.ablodich.smis.appointmentservice.mapper.AppointmentMapper;
import com.ablodich.smis.appointmentservice.repository.AppointmentRepository;
import com.ablodich.smis.starter.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseAppointmentService implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final OutboxService outboxService;
    private final ServiceTopicsProperties serviceTopicsProperties;

    @Override
    @Transactional(readOnly = true)
    public Appointment findAppointmentEntityById(final UUID id) {
        return appointmentRepository.findById(id)
                                    .orElseThrow(() -> new NotFoundException(StringConstants.APPOINTMENT_NOT_FOUND + id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentDto findAppointmentById(final UUID id) {
        Appointment appointment = findAppointmentEntityById(id);
        return appointmentMapper.appointmentToAppointmentDto(appointment);
    }

    @Override
    @Transactional
    public AppointmentDto createAppointment(final PostAppointmentDto appointmentDto) {
        var appointment = appointmentMapper.postAppointmentDtoToAppointment(appointmentDto);
        var savedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.appointmentToAppointmentDto(savedAppointment);
    }

    @Override
    @Transactional
    public AppointmentDto updateAppointment(final PutAppointmentDto appointmentDto) {
        var appointment = findAppointmentEntityById(appointmentDto.id());
        appointmentMapper.updateAppointmentFromPutAppointmentDto(appointment, appointmentDto);
        var updatedAppointment = appointmentRepository.save(appointment);
        return appointmentMapper.appointmentToAppointmentDto(updatedAppointment);
    }

    @Override
    @Transactional
    public void validate(final AppointmentValidationEvent event) {
        AppointmentValidationEventResult result;
        try {
            findAppointmentEntityById(event.getAppointmentId());
            result = new AppointmentValidationEventResult(event.getTaskId(), true);
            log.debug("Отправляем результат валидации записи к врачу: {}", result);
            outboxService.sendMessage(event.getTaskId().toString(),
                                      serviceTopicsProperties.getAppointmentValidationResultTopic(),
                                      result);
        } catch (NotFoundException e) {
            result = new AppointmentValidationEventResult(event.getTaskId(), false);
            log.debug("Отправляем результат валидации записи к врачу: {}", result);
            outboxService.sendMessage(event.getTaskId().toString(),
                                      serviceTopicsProperties.getAppointmentValidationResultTopic(),
                                      result);
        }
    }

    @Override
    public AppointmentRepository getRepository() {
        return appointmentRepository;
    }
}
