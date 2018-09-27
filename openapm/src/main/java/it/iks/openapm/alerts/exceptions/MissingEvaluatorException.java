package it.iks.openapm.alerts.exceptions;

/**
 * Thrown when an evaluator cannot be defined from given parameters
 */
public class MissingEvaluatorException extends RuntimeException {
    public MissingEvaluatorException(String message) {
        super(message);
    }

    public MissingEvaluatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
