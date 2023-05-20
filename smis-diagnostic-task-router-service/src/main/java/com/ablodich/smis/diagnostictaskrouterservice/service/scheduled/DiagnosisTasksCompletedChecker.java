package com.ablodich.smis.diagnostictaskrouterservice.service.scheduled;

import com.ablodich.smis.diagnostictaskrouterservice.service.DiagnosisTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiagnosisTasksCompletedChecker {
    private final DiagnosisTaskService diagnosisTaskService;

    @SchedulerLock(name = "checkForCompletedTasks",
                   lockAtLeastFor = "${service.schedulers.checkForCompletedTasks.lockAtLeastFor:5000}",
                   lockAtMostFor = "${service.schedulers.checkForCompletedTasks.lockAtMostFor:10000}")
    @Scheduled(fixedDelayString = "${service.schedulers.checkForCompletedTasks.delayMs}")
    public void checkForCompletedTasks() {
        diagnosisTaskService.checkForCompletedTasks();
    }
}
