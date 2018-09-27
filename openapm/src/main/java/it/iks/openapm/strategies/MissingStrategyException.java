package it.iks.openapm.strategies;

/**
 * Thrown when a strategy cannot be defined from given parameters
 */
public class MissingStrategyException extends RuntimeException {
    public MissingStrategyException(String message) {
        super(message);
    }

    public MissingStrategyException(String message, Throwable cause) {
        super(message, cause);
    }
}
