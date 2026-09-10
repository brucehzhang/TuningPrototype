package com.tuning.tuningprototype.exceptions;

import lombok.Getter;

@Getter
public class UserActivityException extends RuntimeException {

    private final boolean isUserError;

    public UserActivityException(String message, boolean isUserError) {
        this.isUserError = isUserError;
        super(message);
    }

}
