package org.ehr.roundit.domain.entities;

public class UnableToProvideDataException extends Exception {
    public UnableToProvideDataException(String message) {
        super(message);
    }
}
