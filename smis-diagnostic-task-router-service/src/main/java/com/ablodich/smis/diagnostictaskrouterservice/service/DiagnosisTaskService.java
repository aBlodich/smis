package com.ablodich.smis.diagnostictaskrouterservice.service;

import com.ablodich.smis.common.event.AppointmentValidationEvent;
import com.ablodich.smis.common.event.AppointmentValidationEventResult;
import com.ablodich.smis.common.event.DiagnosisTaskEvent;
import com.ablodich.smis.common.event.DiagnosisTaskEventResult;
import com.ablodich.smis.common.event.DiagnosisTaskResultInfo;
import com.ablodich.smis.common.event.DiagnosisTaskResultNotificationEvent;
import com.ablodich.smis.common.event.LinkAppointmentEvent;
import com.ablodich.smis.common.event.LinkAppointmentResultEvent;
import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventResultState;
import com.ablodich.smis.common.event.enumerate.PredictionResult;
import com.ablodich.smis.common.event.enumerate.DiagnosisTaskEventState;
import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.diagnostictaskrouterservice.config.ServiceTopicsProperties;
import com.ablodich.smis.diagnostictaskrouterservice.dto.DiagnosisTaskDto;
import com.ablodich.smis.diagnostictaskrouterservice.entity.CheckingService;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTask;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisTaskResult;
import com.ablodich.smis.diagnostictaskrouterservice.entity.DiagnosisType;
import com.ablodich.smis.diagnostictaskrouterservice.entity.enumerate.DiagnosisTaskState;
import com.ablodich.smis.diagnostictaskrouterservice.mapper.DiagnosisTaskMapper;
import com.ablodich.smis.diagnostictaskrouterservice.repository.DiagnosisTaskRepository;
import com.ablodich.smis.diagnostictaskrouterservice.repository.DiagnosisTaskResultRepository;
import com.ablodich.smis.starter.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisTaskService {
    private final DiagnosisTaskRepository diagnosisTaskRepository;
    private final DiagnosisTaskResultRepository diagnosisTaskResultRepository;
    private final DiagnosisTypeService diagnosisTypeService;
    private final CheckingServiceService checkingServiceService;
    private final OutboxService outboxService;
    private final ServiceTopicsProperties serviceTopicsProperties;
    private final DiagnosisTaskMapper diagnosisTaskMapper;

    @Transactional
    public UUID createTask(final UUID appointmentId,
                           final String diagnosisCode,
                           final String fileId) {
        UUID taskId = UUID.randomUUID();
        DiagnosisType type = diagnosisTypeService.findDiagnosisTypeEntityByCode(diagnosisCode);
        List<CheckingService> checkingServices = checkingServiceService.findCheckingServicesByDiagnosisTypeCode(diagnosisCode);
        DiagnosisTask diagnosisTask = createStartedDiagnosisTask(taskId, type, checkingServices, appointmentId, fileId);
        diagnosisTask = diagnosisTaskRepository.saveAndFlush(diagnosisTask);

        startAppointmentValidationEventIfNeeded(taskId, appointmentId);
        startDiagnosisTask(diagnosisTask, fileId);
        if (Boolean.TRUE.equals(Boolean.TRUE.equals(diagnosisTask.getWaitForAppointmentValidation()))) {
            diagnosisTask.setState(DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_PENDING);
        } else {
            diagnosisTask.setState(DiagnosisTaskState.DIAGNOSIS_PENDING);
        }
        return taskId;
    }

    @Transactional
    public void processAppointmentValidation(final AppointmentValidationEventResult appointmentValidationEventResult) {
        try {
            UUID taskId = appointmentValidationEventResult.getTaskId();
            Optional<DiagnosisTask> taskOptional = diagnosisTaskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                log.error("Пришел результат валидации записи к врачу для несуществующей задачи. Событие будет пропущено");
                return;
            }
            DiagnosisTask task = taskOptional.get();
            if (task.getState() == DiagnosisTaskState.ERROR) {
                log.debug("Пришел результат валидации записи к врачу для задачи, завершенной с ошибкой. Событие будет пропущено");
                return;
            }
            if (Boolean.FALSE.equals(appointmentValidationEventResult.getIsValid())) {
                finishTaskWithError(task, "Задача не прошла валидацию записи к врачу");
                return;
            }
            updateTaskStatus(task, DiagnosisTaskState.APPOINTMENT_VALIDATION_COMPLETED);
        } catch (Exception e) {
            log.error("Ошибка во время обработки результата валидации записи к врачу: ", e);
            finishTaskWithError(appointmentValidationEventResult.getTaskId(), "Ошибка во время обработки результата валидации записи к врачу: " + e.getMessage());
        }
    }

    @Transactional
    public void processDiagnosisTaskEventResult(final DiagnosisTaskEventResult diagnosisTaskEventResult) {
        UUID taskId = diagnosisTaskEventResult.getTaskId();
        Optional<DiagnosisTask> taskOptional = diagnosisTaskRepository.findById(taskId);
        if (taskOptional.isEmpty()) {
            log.error("Пришел результат для несуществующей задачи. Событие будет пропущено");
            return;
        }
        DiagnosisTask task = taskOptional.get();
        if (task.getState() == DiagnosisTaskState.COMPLETED) {
            log.debug("Пришел результат для выполненой задачи. Событие будет пропущено");
            return;
        }
        if (task.getState() == DiagnosisTaskState.ERROR) {
            log.debug("Пришел результат для задачи, завершенной с ошибкой. Событие будет пропущено");
            return;
        }
        if (diagnosisTaskEventResult.getState() == DiagnosisTaskEventResultState.ERROR) {
            finishTaskWithError(task, "Ошибка при выполнении проверки медицинского снимка: " + diagnosisTaskEventResult.getErrorDescription());
            return;
        }
        DiagnosisTaskResult diagnosisTaskResult = new DiagnosisTaskResult();
        diagnosisTaskResult.setDiagnosisTask(task);
        diagnosisTaskResult.setPrediction(diagnosisTaskEventResult.getPrediction());
        diagnosisTaskResult.setSegmentedFileId(diagnosisTaskResult.getSegmentedFileId());
        diagnosisTaskResult.setActive(false);
        if (CollectionUtils.isEmpty(task.getResults())) {
            task.setResults(new HashSet<>(List.of(diagnosisTaskResult)));
        } else {
            task.getResults().add(diagnosisTaskResult);
        }
        task = diagnosisTaskRepository.saveAndFlush(task);
        updateActiveResultForDiagnosisTask(taskId);

        updateTaskStatus(task, DiagnosisTaskState.DIAGNOSIS_COMPLETED);
    }

    @Transactional
    public void processLinkAppointmentEvent(final LinkAppointmentResultEvent event) {
        try {
            UUID taskId = event.getTaskId();
            Optional<DiagnosisTask> taskOptional = diagnosisTaskRepository.findById(taskId);
            if (taskOptional.isEmpty()) {
                log.error("Пришел результат связывания файла с записью к врачу для несуществующей задачи. Событие будет пропущено");
                return;
            }
            DiagnosisTask task = taskOptional.get();
            if (task.getState() == DiagnosisTaskState.ERROR) {
                log.debug("Пришел результат связывания файла с записью к врачу для задачи, завершенной с ошибкой. Событие будет пропущено");
                return;
            }
            if (!event.isResult()) {
                finishTaskWithError(task, event.getErrorDescription());
                return;
            }
            updateTaskStatus(task, DiagnosisTaskState.APPOINTMENT_LINKING_COMPLETED);
        } catch (Exception e) {
            log.error("Ошибка во время обработки результата связывания файла с записью к врачу: ", e);
            finishTaskWithError(event.getTaskId(), "Ошибка во время обработки результата связывания файла с записью к врачу: " + e.getMessage());
        }

    }

    @Transactional
    public DiagnosisTaskDto findByById(final UUID taskId) {
        DiagnosisTask diagnosisTask = diagnosisTaskRepository.findById(taskId).orElseThrow(() -> new NotFoundException("Не найдена задача с Id = " + taskId));
        return diagnosisTaskMapper.diagnosisTaskToDiagnosisTaskDto(diagnosisTask);
    }

    private void updateActiveResultForDiagnosisTask(final UUID taskId) {
        Set<DiagnosisTaskResult> diagnosisTaskResults = diagnosisTaskResultRepository.findByDiagnosisTask_Id(taskId);
        Optional<DiagnosisTaskResult> activeResultOptional = diagnosisTaskResults.stream().filter(DiagnosisTaskResult::getActive).findFirst();
        List<DiagnosisTaskResult> diseasesResults = diagnosisTaskResults.stream()
                                                                      .filter(tr -> tr.getPrediction() == PredictionResult.DISEASE_FOUND)
                                                                      .toList();
        if (activeResultOptional.isEmpty()) {
            if (diseasesResults.isEmpty()) {
                diagnosisTaskResults.iterator().next().setActive(true);
                return;
            }
            diseasesResults.iterator().next().setActive(true);
            return;
        }
        DiagnosisTaskResult activeResult = activeResultOptional.get();
        if (activeResult.getPrediction() == PredictionResult.DISEASE_FOUND) {
            return;
        }
        if (diseasesResults.isEmpty()) {
            return;
        }
        activeResult.setActive(false);
        diseasesResults.iterator().next().setActive(true);
        diagnosisTaskResultRepository.saveAndFlush(activeResult);
        diagnosisTaskResultRepository.saveAndFlush(diseasesResults.iterator().next());
    }

    @Transactional
    public void checkForCompletedTasks() {
        Set<DiagnosisTask> notCompletedTasks = diagnosisTaskRepository.findIncompleteDiagnosisTasks();
        notCompletedTasks.forEach(this::processNotCompletedTask);
    }

    private void processNotCompletedTask(final DiagnosisTask t) {
        if (checkIfTaskReadyForAppointmentLinking(t)) {
            processReadyForAppointmentLiningTask(t);
            return;
        }
        if (checkIfTaskCompleted(t)) {
            processCompletedTask(t);
        }
    }

    private void processReadyForAppointmentLiningTask(final DiagnosisTask task) {
        updateTaskStatus(task, DiagnosisTaskState.APPOINTMENT_LINKING_PENDING);
        sendLinkingAppointmentEvent(task);
    }

    private void sendLinkingAppointmentEvent(final DiagnosisTask task) {
        Optional<DiagnosisTaskResult> diagnosisTaskResultOptional = task.getResults().stream().filter(DiagnosisTaskResult::getActive).findFirst();
        if (diagnosisTaskResultOptional.isEmpty()) {
            finishTaskWithError(task, "Отсутсвует активный результат проверки");
            return;
        }
        DiagnosisTaskResult result = diagnosisTaskResultOptional.get();
        LinkAppointmentEvent event = new LinkAppointmentEvent();
        event.setTaskId(task.getId());
        event.setAppointmentId(task.getAppointmentId());
        event.setSegmentedFileId(result.getSegmentedFileId());
        event.setOriginalFileId(task.getOriginalFileId());
        outboxService.sendMessage(event.getTaskId().toString(), serviceTopicsProperties.getLinkAppointmentEventTopic(), event);
    }

    private boolean checkIfTaskReadyForAppointmentLinking(final DiagnosisTask task) {
        return task.getWaitForAppointmentValidation() && task.getState() == DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_COMPLETED;
    }

    private void processCompletedTask(final DiagnosisTask task) {
        updateTaskStatus(task, DiagnosisTaskState.COMPLETED);
        DiagnosisTaskResultInfo diagnosisTaskResultInfo = chooseResultInfo(task);
        sendTaskResultToNotificationService(task, diagnosisTaskResultInfo);
    }

    private DiagnosisTaskResultInfo chooseResultInfo(final DiagnosisTask task) {
        PredictionResult predictionResult = null;
        String segmentedFileId = null;
        for (DiagnosisTaskResult result : task.getResults()) {
            predictionResult = result.getPrediction();
            segmentedFileId = result.getSegmentedFileId();
            if (predictionResult == PredictionResult.DISEASE_FOUND) {
                break;
            }
        }
        return new DiagnosisTaskResultInfo(predictionResult, segmentedFileId);
    }

    private boolean checkIfTaskCompleted(final DiagnosisTask task) {
        if (Boolean.TRUE.equals(!task.getWaitForAppointmentValidation()) && task.getState() == DiagnosisTaskState.DIAGNOSIS_COMPLETED) {
            return true;
        }
        if (Boolean.TRUE.equals(task.getWaitForAppointmentValidation()) && task.getState() == DiagnosisTaskState.APPOINTMENT_LINKING_COMPLETED) {
            int expectedResultsCount = task.getCheckingServices().size();
            int actualResultsCount = task.getResults() == null ? 0 : task.getResults().size();
            return expectedResultsCount == actualResultsCount;
        }
        return false;

    }

    @Transactional
    public void finishTaskWithError(UUID taskId, final String errorDescription) {
        diagnosisTaskRepository.findById(taskId).ifPresent(t -> finishTaskWithError(t, errorDescription));
    }

    private void finishTaskWithError(DiagnosisTask task, final String errorDescription) {
        updateTaskStatus(task, DiagnosisTaskState.ERROR);
        task.setErrorDescription(errorDescription);
        task.getCheckingServices().forEach(checkingService -> {
            String targetTopic =  serviceTopicsProperties.getDiagnosisServicesTasks().get(checkingService.getServiceName());
            cancelDiagnosisTask(task.getId(), targetTopic);
            sendTaskResultToNotificationService(task, null);
        });
    }

    private void sendTaskResultToNotificationService(final DiagnosisTask task, final DiagnosisTaskResultInfo taskResult) {
        DiagnosisTaskResultNotificationEvent event = new DiagnosisTaskResultNotificationEvent();
        event.setId(task.getId());
        if (task.getState() == DiagnosisTaskState.ERROR) {
            event.setState(DiagnosisTaskEventState.ERROR);
        }
        if (task.getState() == DiagnosisTaskState.CANCELLED) {
            event.setState(DiagnosisTaskEventState.CANCELLED);
        }
        if (task.getState() == DiagnosisTaskState.COMPLETED) {
            event.setState(DiagnosisTaskEventState.COMPLETED);
        }
        event.setErrorDescription(task.getErrorDescription());
        event.setDiagnosisTaskResultInfo(taskResult);
        outboxService.sendMessage(task.getId().toString(), serviceTopicsProperties.getTaskResultNotificationTopic(), event);
    }

    private void cancelDiagnosisTask(final UUID taskId, final String targetTopic) {
        DiagnosisTaskEvent diagnosisTaskEvent = new DiagnosisTaskEvent();
        diagnosisTaskEvent.setId(taskId);
        diagnosisTaskEvent.setState(DiagnosisTaskEventState.CANCELLED);
        outboxService.sendMessage(diagnosisTaskEvent.getId().toString(), targetTopic, diagnosisTaskEvent);
    }

    private void startDiagnosisTask(final DiagnosisTask diagnosisTask, final String fileId) {
        DiagnosisTaskEvent diagnosisTaskEvent = new DiagnosisTaskEvent();
        diagnosisTaskEvent.setId(diagnosisTask.getId());
        diagnosisTaskEvent.setState(DiagnosisTaskEventState.STARTED);
        diagnosisTaskEvent.setFileId(fileId);
        List<String> services = diagnosisTask.getCheckingServices().stream().map(CheckingService::getServiceName).toList();
        services.forEach(s -> sendTaskToTargetService(diagnosisTask, diagnosisTaskEvent, s));
    }

    private void sendTaskToTargetService(final DiagnosisTask diagnosisTask, final DiagnosisTaskEvent diagnosisTaskEvent, final String s) {
        String targetTopic = serviceTopicsProperties.getDiagnosisServicesTasks().get(s);
        if (targetTopic == null) {
            log.error("Отсутствует информация о топике для сервиса проверок '{}'", s);
            return;
        }
        outboxService.sendMessage(diagnosisTask.getId().toString(), targetTopic, diagnosisTaskEvent);
    }

    private void startAppointmentValidationEventIfNeeded(final UUID taskId, final UUID appointmentId) {
        if (appointmentId == null) {
            return;
        }
        AppointmentValidationEvent appointmentValidationEvent = new AppointmentValidationEvent(taskId, appointmentId);
        outboxService.sendMessage(taskId.toString(), serviceTopicsProperties.getAppointmentValidationEventTopic(), appointmentValidationEvent);
    }

    private DiagnosisTask createStartedDiagnosisTask(final UUID taskId,
                                                     final DiagnosisType type,
                                                     final List<CheckingService> checkingService,
                                                     final UUID appointmentId,
                                                     final String fileId) {
        DiagnosisTask diagnosisTask = new DiagnosisTask();
        if (taskId != null) {
            diagnosisTask.setId(taskId);
        }
        diagnosisTask.setDiagnosisType(type);
        diagnosisTask.setCheckingServices(new HashSet<>(checkingService));
        diagnosisTask.setState(DiagnosisTaskState.STARTED);
        diagnosisTask.setWaitForAppointmentValidation(appointmentId != null);
        diagnosisTask.setAppointmentId(appointmentId);
        diagnosisTask.setOriginalFileId(fileId);
        return diagnosisTask;
    }

    private void updateTaskStatus(final DiagnosisTask task, final DiagnosisTaskState diagnosisTaskState) {
        if (task.getState() == DiagnosisTaskState.STARTED) {
            task.setState(diagnosisTaskState);
            return;
        }
        if (diagnosisTaskState == DiagnosisTaskState.ERROR) {
            task.setState(diagnosisTaskState);
            return;
        }
        if (diagnosisTaskState == DiagnosisTaskState.COMPLETED) {
            task.setState(diagnosisTaskState);
            return;
        }
        DiagnosisTaskState stateResult = calculateDiagnosisTaskState(task, diagnosisTaskState);
        if (stateResult == null) {
            return;
        }
        task.setState(stateResult);
    }

    private DiagnosisTaskState calculateDiagnosisTaskState(final DiagnosisTask task, final DiagnosisTaskState diagnosisTaskState) {
        DiagnosisTaskState stateResult = null;
        int expectedResultsCount = task.getCheckingServices().size();
        int actualResultsCount = task.getResults() == null ? 0 : task.getResults().size();

        if (task.getState() == DiagnosisTaskState.DIAGNOSIS_PENDING && diagnosisTaskState == DiagnosisTaskState.DIAGNOSIS_COMPLETED) {
            stateResult = DiagnosisTaskState.DIAGNOSIS_COMPLETED;
        }
        if (task.getState() == DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_PENDING
                && diagnosisTaskState == DiagnosisTaskState.APPOINTMENT_VALIDATION_COMPLETED) {
            stateResult = DiagnosisTaskState.APPOINTMENT_VALIDATION_COMPLETED_AND_DIAGNOSIS_PENDING;
        }
        if (task.getState() == DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_PENDING
                && diagnosisTaskState == DiagnosisTaskState.DIAGNOSIS_COMPLETED) {
            stateResult = DiagnosisTaskState.DIAGNOSIS_COMPLETED_AND_APPOINTMENT_VALIDATION_PENDING;
        }
        if (task.getState() == DiagnosisTaskState.APPOINTMENT_VALIDATION_COMPLETED_AND_DIAGNOSIS_PENDING
                && diagnosisTaskState == DiagnosisTaskState.DIAGNOSIS_COMPLETED) {
            if (expectedResultsCount != actualResultsCount) {
                return null;
            }
            stateResult = DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_COMPLETED;
        }
        if (task.getState() == DiagnosisTaskState.DIAGNOSIS_COMPLETED_AND_APPOINTMENT_VALIDATION_PENDING
                && diagnosisTaskState == DiagnosisTaskState.APPOINTMENT_VALIDATION_COMPLETED) {
            stateResult = DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_COMPLETED;
        }
        if (Boolean.TRUE.equals(!task.getWaitForAppointmentValidation() && task.getState() == DiagnosisTaskState.DIAGNOSIS_COMPLETED)
                && diagnosisTaskState == DiagnosisTaskState.APPOINTMENT_LINKING_PENDING) {
            stateResult = diagnosisTaskState;
        }
        if (Boolean.TRUE.equals(task.getWaitForAppointmentValidation() && task.getState() == DiagnosisTaskState.APPOINTMENT_VALIDATION_AND_DIAGNOSIS_COMPLETED)
                && diagnosisTaskState == DiagnosisTaskState.APPOINTMENT_LINKING_PENDING) {
            stateResult = diagnosisTaskState;
        }
        if (task.getState() == DiagnosisTaskState.APPOINTMENT_LINKING_PENDING && diagnosisTaskState == DiagnosisTaskState.APPOINTMENT_LINKING_COMPLETED) {
            stateResult = diagnosisTaskState;
        }
        return stateResult;
    }
}