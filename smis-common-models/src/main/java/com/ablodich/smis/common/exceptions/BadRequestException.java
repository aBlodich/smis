package com.ablodich.smis.common.exceptions;

import java.net.HttpURLConnection;

public class BadRequestException extends ApiException {

    public BadRequestException(final String message) {
        super(HttpURLConnection.HTTP_BAD_REQUEST, message);
    }
}