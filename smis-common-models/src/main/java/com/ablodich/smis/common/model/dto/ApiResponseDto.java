package com.ablodich.smis.common.model.dto;

import lombok.Builder;
import lombok.Getter;

import java.net.HttpURLConnection;

@Builder
@Getter
public class ApiResponseDto<T> {
    private int status;
    private T result;
    private String error;

    public static <E> ApiResponseDto<E> ok(E result) {
        return build(result, null, HttpURLConnection.HTTP_OK);
    }

    public static <E> ApiResponseDto<E> notFound(E result, String error) {
        return build(result, error, HttpURLConnection.HTTP_NOT_FOUND);
    }

    public static <E> ApiResponseDto<E> badRequest(E result, String error) {
        return build(result, error, HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static <E> ApiResponseDto<E> build(E result, String error, int status) {
        return ApiResponseDto.<E>builder()
                             .status(status)
                             .result(result)
                             .error(error)
                             .build();
    }
}