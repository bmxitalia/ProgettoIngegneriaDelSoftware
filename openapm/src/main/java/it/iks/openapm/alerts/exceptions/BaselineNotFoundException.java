package it.iks.openapm.alerts.exceptions;

/**
 * Thrown when a baseline is not found
 */
public class BaselineNotFoundException extends Exception {
    public BaselineNotFoundException(String message) {
        super(message);
    }
}
