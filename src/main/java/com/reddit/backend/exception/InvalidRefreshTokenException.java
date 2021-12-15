package com.reddit.backend.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String msg) {
        super(msg);
    }
}
