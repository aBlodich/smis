package com.ablodich.smis.diagnostictaskrouterservice.service;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.diagnostictaskrouterservice.dto.MultipartFileImpl;
import com.ablodich.smis.diagnostictaskrouterservice.exception.DiagnosticTaskException;
import com.ablodich.smis.diagnostictaskrouterservice.service.client.FileServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileServiceClient fileServiceClient;

    public String uploadFile(final byte[] content, final String originalFileName, final String contentType) {
        try {
            MultipartFile file = new MultipartFileImpl(originalFileName, contentType, content);
            ApiResponseDto<String> result = fileServiceClient.uploadFile(file);
            throwIfErrorStatus(result);
            return result.getResult();
        } catch (FeignException.FeignClientException e) {
            processFeignError(e);
        }
        return null;
    }

    private static void throwIfErrorStatus(final ApiResponseDto<String> result) {
        if (!HttpStatus.valueOf(result.getStatus()).isError()) {
            return;
        }
        String errorMsg = "Ошибка при сохранении файла: " + result.getError();
        log.error(errorMsg);
        throw new DiagnosticTaskException(errorMsg);
    }

    private void processFeignError(final FeignException.FeignClientException e) {
        log.error("Ошибка при попытке сохранения файла:\n", e);
        throw new DiagnosticTaskException(e.getMessage());
    }
}
