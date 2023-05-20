package com.ablodich.smis.brainmriclassificationservice.exception;

public class PredictionException extends RuntimeException {

    public PredictionException(final String message) {
        super(message);
    }

    public PredictionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
