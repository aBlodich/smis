package com.ablodich.smis.brainmriclassificationservice.service.client;

import com.ablodich.smis.brainmriclassificationservice.config.FeignSupportConfig;
import com.ablodich.smis.common.model.dto.ApiResponseDto;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@FeignClient(value = "${feign.file-service.service-name}",
             url = "${feign.file-service.service-url}",
             configuration = FeignSupportConfig.class)
public interface FileServiceClient {

    @PostMapping("/v1/files")
    ApiResponseDto<String> uploadFile(@RequestPart MultipartFile file);

    @GetMapping(value = "/v1/files/{fileId}")
    Response getFile(@PathVariable String fileId);
}
