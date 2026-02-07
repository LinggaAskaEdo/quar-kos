package com.otis.usersvc.exception;

public class SqlQueryLoadException extends RuntimeException {
    public SqlQueryLoadException(String message) {
        super(message);
    }

    public SqlQueryLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
