package com.ablodich.smis.brainmriclassificationservice.service;

import com.ablodich.smis.brainmriclassificationservice.exception.GettingFileException;
import com.ablodich.smis.brainmriclassificationservice.service.client.FileServiceClient;
import feign.FeignException;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileServiceClient fileServiceClient;


    public byte[] getFileBytes(final String fileId) {
        try {
            return IOUtils.toByteArray(getFileInputStream(fileId));
        }
        catch (IOException e) {
            log.error("Ошибка во время чтения файла:\n", e);
            throw new GettingFileException("Ошибка во время чтения файла: " + e.getMessage());
        }
    }

    public InputStream getFileInputStream(final String fileId) {
        try {
            Response response = fileServiceClient.getFile(fileId);
            return response.body().asInputStream();

        } catch (FeignException.FeignClientException e) {
            log.error("Ошибка во время получения файла:\n", e);
            throw new GettingFileException("Ошибка во время получения файла: " + e.getMessage());
        } catch (IOException e) {
            log.error("Ошибка во время чтения файла:\n", e);
            throw new GettingFileException("Ошибка во время чтения файла: " + e.getMessage());
        }
    }
}
