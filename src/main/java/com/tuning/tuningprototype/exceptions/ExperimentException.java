package com.tuning.tuningprototype.exceptions;

import lombok.Getter;

@Getter
public class ExperimentException extends RuntimeException {

    private final boolean isUserError;

    public ExperimentException(String message, boolean isUserError) {
        this.isUserError = isUserError;
        super(message);
    }

}
