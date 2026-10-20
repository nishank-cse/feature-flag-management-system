package com.github.nishank.featureflags.exception;

public class IllegalSelfOperationException extends RuntimeException {

    public IllegalSelfOperationException(String message) {
        super(message);
    }
}
