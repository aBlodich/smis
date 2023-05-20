package com.ablodich.smis.common.exceptions;

import java.net.HttpURLConnection;

public class NotFoundException extends ApiException {

    public NotFoundException(final String message) {
        super(HttpURLConnection.HTTP_NOT_FOUND, message);
    }
}
