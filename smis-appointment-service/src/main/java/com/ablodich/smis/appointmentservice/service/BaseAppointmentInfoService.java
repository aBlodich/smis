package com.ablodich.smis.appointmentservice.service;

import com.ablodich.smis.appointmentservice.config.ServiceTopicsProperties;
import com.ablodich.smis.appointmentservice.entity.AppointmentInfoAttachment;
import com.ablodich.smis.common.event.LinkAppointmentEvent;
import com.ablodich.smis.common.event.LinkAppointmentResultEvent;
import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.AppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PostAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.dto.appointmentinfo.PutAppointmentInfoDto;
import com.ablodich.smis.appointmentservice.entity.Appointment;
import com.ablodich.smis.appointmentservice.entity.AppointmentInfo;
import com.ablodich.smis.appointmentservice.mapper.AppointmentInfoMapper;
import com.ablodich.smis.appointmentservice.repository.AppointmentInfoRepository;
import com.ablodich.smis.starter.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Set;
import java.util.UUID;

import static com.ablodich.smis.appointmentservice.constants.StringConstants.APPOINTMENT_INFO_NOT_FOUND;
import static com.ablodich.smis.appointmentservice.constants.StringConstants.APPOINTMENT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class BaseAppointmentInfoService implements AppointmentInfoService {
    private final AppointmentInfoRepository appointmentInfoRepository;
    private final AppointmentInfoMapper appointmentInfoMapper;
    private final AppointmentService appointmentService;
    private final OutboxService outboxService;
    private final ServiceTopicsProperties serviceTopicsProperties;

    @Override
    @Transactional(readOnly = true)
    public AppointmentInfo findAppointmentInfoEntityById(final UUID id) {
        return appointmentInfoRepository.findById(id)
                                        .orElseThrow(() -> new NotFoundException(APPOINTMENT_INFO_NOT_FOUND + id));
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentInfoDto findAppointmentInfoById(final UUID id) {
        var appointmentInfo = findAppointmentInfoEntityById(id);
        return appointmentInfoMapper.appointmentInfoToAppointmentInfoDto(appointmentInfo);
    }

    @Override
    @Transactional
    public AppointmentInfoDto createAppointmentInfo(final UUID appointmentId, final PostAppointmentInfoDto appointmentInfoDto) {
        Appointment appointment = appointmentService.findAppointmentEntityById(appointmentId);
        AppointmentInfo appointmentInfo = appointmentInfoMapper.postAppointmentInfoDtoToAppointmentInfo(appointmentInfoDto);
        appointmentInfo.setAppointment(appointment);
        AppointmentInfo savedAppointmentInfo = appointmentInfoRepository.save(appointmentInfo);
        return appointmentInfoMapper.appointmentInfoToAppointmentInfoDto(savedAppointmentInfo);
    }

    @Override
    @Transactional
    public AppointmentInfoDto updateAppointmentInfo(final PutAppointmentInfoDto appointmentInfoDto) {
        var appointmentInfo = findAppointmentInfoEntityById(appointmentInfoDto.id());
        appointmentInfoMapper.updateAppointmentInfoFromPutAppointmentInfoDto(appointmentInfo, appointmentInfoDto);
        var savedAppointmentInfo = appointmentInfoRepository.save(appointmentInfo);
        return appointmentInfoMapper.appointmentInfoToAppointmentInfoDto(savedAppointmentInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentInfoDto findAppointmentInfoByAppointmentId(final UUID appointmentId) {
        throwIfAppointmentNotExists(appointmentId);
        var appointment = appointmentInfoRepository.findByAppointmentId(appointmentId)
                                                   .orElseThrow(() -> new NotFoundException(APPOINTMENT_INFO_NOT_FOUND + appointmentId));
        return appointmentInfoMapper.appointmentInfoToAppointmentInfoDto(appointment);
    }

    @Override
    @Transactional
    public void createAttachmentForAppointmentInfo(final LinkAppointmentEvent event) {
        try {
            AppointmentInfo appointmentInfo = findAppointmentInfoEntityById(event.getAppointmentId());
            AppointmentInfoAttachment appointmentInfoAttachment = new AppointmentInfoAttachment();
            var id = AppointmentInfoAttachment.AppointmentInfoAttachmentId.of(appointmentInfo, event.getTaskId());
            appointmentInfoAttachment.setId(id);
            appointmentInfoAttachment.setOriginalFileId(event.getOriginalFileId());
            appointmentInfoAttachment.setSegmentedFileId(event.getSegmentedFileId());

            if (CollectionUtils.isEmpty(appointmentInfo.getAttachments())) {
                appointmentInfo.setAttachments(Set.of(appointmentInfoAttachment));
            } else {
                appointmentInfo.getAttachments().add(appointmentInfoAttachment);
            }
        } catch (NotFoundException e) {
            processNotFoundExceptionOnCreatingAttachment(event);
        } catch (Exception e) {
            processUnexpectedExceptionOnCreatingAttachment(event, e);
        }
        sendLinkAppointmentEventResultCompleted(event);
    }

    @Transactional
    public void processUnexpectedExceptionOnCreatingAttachment(final LinkAppointmentEvent event, final Exception e) {
        String errorMsg = MessageFormat.format("Ошибка во время связывания файлов с записью к врачу с id = {0}.\nПодробности: {1}",
                                               event.getAppointmentId(),
                                               e.getMessage());
        AppointmentInfo appointmentInfo = findAppointmentInfoEntityById(event.getAppointmentId());
        if (CollectionUtils.isEmpty(appointmentInfo.getAttachments())) {
            sendLinkAppointmentEventResultError(event, errorMsg);
            return;
        }
        var id = AppointmentInfoAttachment.AppointmentInfoAttachmentId.of(appointmentInfo, event.getTaskId());
        appointmentInfo.getAttachments().removeIf(at -> at.getId().equals(id));
        sendLinkAppointmentEventResultError(event, errorMsg);
    }

    public void processNotFoundExceptionOnCreatingAttachment(final LinkAppointmentEvent event) {
        String errorMsg = "Ошибка во время связывания файлов с записью к врачу. Отсутсвует информация о записи к врачу с id = " + event.getAppointmentId();
        log.debug(errorMsg);
        sendLinkAppointmentEventResultError(event, errorMsg);
    }

    private void sendLinkAppointmentEventResultError(final LinkAppointmentEvent event, final String errorMsg) {
        log.debug("Отправляем результат связывания файла с записью к врачу: {}", event);
        LinkAppointmentResultEvent resultEvent = new LinkAppointmentResultEvent();
        resultEvent.setResult(false);
        resultEvent.setTaskId(event.getTaskId());
        resultEvent.setErrorDescription(errorMsg);
        outboxService.sendMessage(resultEvent.getTaskId().toString(), serviceTopicsProperties.getLinkAppointmentResultTopic(), resultEvent);
    }

    private void sendLinkAppointmentEventResultCompleted(final LinkAppointmentEvent event) {
        log.debug("Отправляем результат связывания файла с записью к врачу: {}", event);
        LinkAppointmentResultEvent resultEvent = new LinkAppointmentResultEvent();
        resultEvent.setResult(true);
        resultEvent.setTaskId(event.getTaskId());
        outboxService.sendMessage(resultEvent.getTaskId().toString(), serviceTopicsProperties.getLinkAppointmentResultTopic(), resultEvent);
    }

    private void throwIfAppointmentNotExists(final UUID appointmentId) {
        if (appointmentService.getRepository().existsById(appointmentId)) {
            return;
        }
        throw new NotFoundException(APPOINTMENT_NOT_FOUND + appointmentId);
    }

    @Override
    public AppointmentInfoRepository getRepository() {
        return appointmentInfoRepository;
    }
}
