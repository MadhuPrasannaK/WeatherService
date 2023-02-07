package org.service;

public class Validation {
    public String message;
    public ValidationStatus status;

    public Validation(ValidationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

enum ValidationStatus {
    SUCCESS, FAILURE
}
