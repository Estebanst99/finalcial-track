package com.estebanst99.financialtrack.exception;

public class CategoryServiceException extends Exception {
    public CategoryServiceException(String message) {
        super(message);
    }

    public CategoryServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
