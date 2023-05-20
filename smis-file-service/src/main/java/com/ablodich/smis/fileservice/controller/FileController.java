package com.ablodich.smis.fileservice.controller;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.fileservice.model.GetFileWrapper;
import com.ablodich.smis.fileservice.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/v1/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @Operation(summary = "Загрузка файла в хранилище")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponseDto<String> uploadFile(@RequestPart MultipartFile file) throws IOException {
        String fileId = fileService.uploadFile(file.getBytes(), file.getContentType(), "." + FileNameUtils.getExtension(file.getOriginalFilename()));
        return ApiResponseDto.ok(fileId);
    }

    @Operation(summary = "Получение файла из хранилища")
    @GetMapping(value = "/{fileId}")
    public StreamingResponseBody getFile(@PathVariable String fileId, HttpServletResponse response) {
        GetFileWrapper getFileWrapper = fileService.getFile(fileId);
        response.addHeader("Content-disposition", "attachment; filename=\"" + fileId + "\"");
        if (getFileWrapper.getContentType() != null) {
            response.setContentType(getFileWrapper.getContentType());
        }
        if (getFileWrapper.getContentLength() != -1) {
            response.setContentLengthLong(getFileWrapper.getContentLength());
        }
        return out -> {
            InputStream is = new ByteArrayInputStream(getFileWrapper.getContent());
            IOUtils.copy(is, response.getOutputStream());
        };
    }

    @Operation(summary = "Удаление файла из хранилища")
    @DeleteMapping("/{fileId}")
    public ApiResponseDto<String> deleteFile(@PathVariable String fileId) {
        return ApiResponseDto.ok(fileService.deleteFile(fileId));
    }

    @Operation(summary = "Проверка на существование файла")
    @GetMapping("/{fileId}/exists")
    public ApiResponseDto<Boolean> fileExists(@PathVariable String fileId) {
        return ApiResponseDto.ok(fileService.fileExists(fileId));
    }
}
