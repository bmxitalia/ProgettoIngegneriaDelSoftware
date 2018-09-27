package it.iks.openapm.actions.exceptions;

/**
 * Thrown when an action cannot be executed
 */
public class ActionException extends RuntimeException {
    public ActionException(String message) {
        super(message);
    }

    public ActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
