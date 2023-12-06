package org.ehr.ambassador.domain.entities;

public class ProcessingFailedException extends Exception {
    public ProcessingFailedException(String message) {
        super(message);
    }

    public ProcessingFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
