package com.ablodich.smis.starter.outbox.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class OutboxException extends RuntimeException {

    public OutboxException(final String message) {
        super(message);
    }

    public OutboxException(final JsonProcessingException e) {
        super(e);
    }
}
