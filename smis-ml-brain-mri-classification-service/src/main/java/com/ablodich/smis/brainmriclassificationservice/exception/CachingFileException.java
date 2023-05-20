package com.ablodich.smis.brainmriclassificationservice.exception;

public class CachingFileException extends RuntimeException {

    public CachingFileException(final String message) {
        super(message);
    }

    public CachingFileException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
