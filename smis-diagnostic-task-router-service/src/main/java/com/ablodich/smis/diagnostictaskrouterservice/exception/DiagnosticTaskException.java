package com.ablodich.smis.diagnostictaskrouterservice.exception;

public class DiagnosticTaskException extends RuntimeException {

    public DiagnosticTaskException(final String message) {
        super(message);
    }

    public DiagnosticTaskException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
