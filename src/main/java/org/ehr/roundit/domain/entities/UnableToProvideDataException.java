package org.ehr.roundit.domain.entities;

public class UnableToProvideDataException extends Exception {
    public UnableToProvideDataException(String message) {
        super(message);
    }

    public UnableToProvideDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
