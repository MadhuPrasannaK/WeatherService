package org.weather.validations;

/**
 * Class that represents validation state - contains a message and a status field
 */
public class Validation {
    public String message;
    public ValidationStatus status;

    public Validation(ValidationStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}