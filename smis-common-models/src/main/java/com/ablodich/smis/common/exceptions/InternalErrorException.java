package com.ablodich.smis.common.exceptions;

import java.net.HttpURLConnection;

public class InternalErrorException extends ApiException {

    public InternalErrorException() {
        this("Произошла внутренняя ошибка сервера, побробуйте сделать запрос позже");
    }

    public InternalErrorException(final String message) {
        super(HttpURLConnection.HTTP_INTERNAL_ERROR, message);
    }
}
