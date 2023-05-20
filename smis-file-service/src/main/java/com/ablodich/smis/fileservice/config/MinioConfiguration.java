package com.ablodich.smis.fileservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MinioProperties.class)
public class MinioConfiguration {
    private final MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient()
            throws IOException, NoSuchAlgorithmException, InvalidKeyException, ServerException, InsufficientDataException, ErrorResponseException,
            InvalidResponseException, XmlParserException, InternalException {
        MinioClient minioClient = buildMinioClient();

        createBucketIfNotExists(minioClient);

        return minioClient;
    }

    private void createBucketIfNotExists(final MinioClient minioClient)
            throws ErrorResponseException, InsufficientDataException, InternalException, InvalidKeyException, InvalidResponseException, IOException,
            NoSuchAlgorithmException, ServerException, XmlParserException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getDefaultBucket()).build());
        if (!found) {
            log.info("Отсутсвует бакет в объектном хранилище");
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getDefaultBucket()).build());
            log.info("Бакет создан");
        }
    }

    @NotNull
    private MinioClient buildMinioClient() {
        return MinioClient.builder()
                          .endpoint(minioProperties.getEndpoint())
                          .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                          .build();
    }
}
