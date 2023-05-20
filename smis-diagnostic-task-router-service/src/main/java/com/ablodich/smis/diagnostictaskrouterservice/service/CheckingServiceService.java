package com.ablodich.smis.diagnostictaskrouterservice.service;

import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.diagnostictaskrouterservice.entity.CheckingService;
import com.ablodich.smis.diagnostictaskrouterservice.repository.CheckingServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.ablodich.smis.diagnostictaskrouterservice.constants.Constants.CHECKING_SERVICE_NOT_FOUND_BY_CODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckingServiceService {
    private final CheckingServiceRepository checkingServiceRepository;

    @Transactional(readOnly = true)
    public List<CheckingService> findCheckingServicesByDiagnosisTypeCode(String diagnosisCode) {
        List<CheckingService> checkingServices = checkingServiceRepository.findByDiagnosisType_Code(diagnosisCode);
        if (checkingServices.isEmpty()) {
            throw new NotFoundException(CHECKING_SERVICE_NOT_FOUND_BY_CODE + diagnosisCode);
        }
        return checkingServices;
    }
}
