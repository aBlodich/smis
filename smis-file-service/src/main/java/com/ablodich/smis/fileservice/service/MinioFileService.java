package com.ablodich.smis.fileservice.service;

import com.ablodich.smis.common.exceptions.InternalErrorException;
import com.ablodich.smis.common.exceptions.NotFoundException;
import com.ablodich.smis.fileservice.config.MinioProperties;
import com.ablodich.smis.fileservice.model.GetFileWrapper;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;

import static com.ablodich.smis.fileservice.constants.Constants.FILE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileService implements FileService {
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(final byte[] fileBytes, final String contentType, final String extension) {
        try {
            ObjectWriteResponse response = minioClient.putObject(PutObjectArgs.builder().bucket(minioProperties.getDefaultBucket())
                                                                              .object(UUID.randomUUID()+extension)
                                                                              .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                                                                              .contentType(contentType)
                                                                              .build());
            return response.object();
        } catch (Exception e) {
            log.error("Ошибка во время загрузки файла в объектное хранилище:\n",e);
            throw new InternalErrorException();
        }
    }

    @Override
    public GetFileWrapper getFile(final String fileId) {
        throwIfFileNotExists(fileId);
        try {
            GetObjectResponse getObjectResponse = minioClient.getObject(GetObjectArgs.builder()
                                                                                     .bucket(minioProperties.getDefaultBucket())
                                                                                     .object(fileId).build());
            return buildGetWrapperFromGetObjectResponse(fileId, getObjectResponse);
        } catch (Exception e) {
            log.error("Ошибка при получении файла из объектного хранилища:\n", e);
            throw new InternalErrorException();
        }
    }

    @Override
    public boolean fileExists(final String fileId) {
        try {
            minioClient.statObject(StatObjectArgs.builder().bucket(minioProperties.getDefaultBucket()).object(fileId).build());
            return true;

        } catch (ErrorResponseException e) {
            return false;
        } catch (Exception e) {
            log.error("Произошла ошибка при проверке соществования файла в объектном хранилище:\n", e);
            throw new InternalErrorException();
        }
    }

    @Override
    public String deleteFile(final String fileId) {
        throwIfFileNotExists(fileId);
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                                                     .bucket(minioProperties.getDefaultBucket())
                                                     .object(fileId).build());
            return fileId;
        } catch (Exception e) {
            log.error("Произошла ошибка при удалении файла из объектного хранилища:\n", e);
            throw new InternalErrorException();
        }
    }

    private void throwIfFileNotExists(final String fileId) {
        if (!fileExists(fileId)) {
            throw new NotFoundException(FILE_NOT_FOUND + fileId);
        }
    }

    @NotNull
    private static GetFileWrapper buildGetWrapperFromGetObjectResponse(final String fileId, final GetObjectResponse getObjectResponse) throws IOException {
        Headers headers = getObjectResponse.headers();
        byte[] content = getObjectResponse.readAllBytes();
        int contentLength = -1;
        if (headers == null) {
            return new GetFileWrapper(null, contentLength, content, fileId);
        }
        String contentLengthString = headers.get(HttpHeaders.CONTENT_LENGTH);
        if (contentLengthString != null) {
            contentLength = Integer.parseInt(contentLengthString);
        }
        String contentType = headers.get(HttpHeaders.CONTENT_TYPE);
        return new GetFileWrapper(contentType, contentLength, content, fileId);
    }
}
