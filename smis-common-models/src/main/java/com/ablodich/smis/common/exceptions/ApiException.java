package com.ablodich.smis.common.exceptions;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    protected final int httpErrorCode;

    public ApiException(int httpErrorCode, String message) {
        super(message);
        this.httpErrorCode = httpErrorCode;
    }
}