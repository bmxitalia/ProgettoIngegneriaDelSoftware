package it.iks.openapm.alerts.exceptions;

/**
 * Thrown when a verifier cannot be defined from given parameters
 */
public class MissingVerifierException extends RuntimeException {
    public MissingVerifierException(String message) {
        super(message);
    }

    public MissingVerifierException(String message, Throwable cause) {
        super(message, cause);
    }
}
