package it.iks.openapm.actions.exceptions;

/**
 * Thrown when an action cannot be defined from given parameters
 */
public class MissingActionException extends ActionException {
    public MissingActionException(String message) {
        super(message);
    }

    public MissingActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
