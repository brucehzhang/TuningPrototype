package com.tuning.tuningprototype.exceptions;

public class ExperimentException extends RuntimeException {

    private final boolean isUserError;

    public ExperimentException(String message, boolean isUserError) {
        this.isUserError = isUserError;
        super(message);
    }

    public boolean isUserError() {
        return isUserError;
    }
}
