package com.ablodich.smis.brainmriclassificationservice.exception;

public class GettingFileException extends RuntimeException {

    public GettingFileException(final String message) {
        super(message);
    }

    public GettingFileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
