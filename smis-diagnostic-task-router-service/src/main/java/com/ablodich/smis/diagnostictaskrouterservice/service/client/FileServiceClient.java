package com.ablodich.smis.diagnostictaskrouterservice.service.client;

import com.ablodich.smis.common.model.dto.ApiResponseDto;
import com.ablodich.smis.diagnostictaskrouterservice.config.FeignSupportConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "${feign.file-service.service-name}",
             url = "${feign.file-service.service-url}",
             configuration = FeignSupportConfig.class)
public interface FileServiceClient {

    @PostMapping("/v1/files")
    ApiResponseDto<String> uploadFile(@RequestPart MultipartFile file);

}
