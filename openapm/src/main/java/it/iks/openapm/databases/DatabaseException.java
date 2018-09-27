package it.iks.openapm.databases;

/**
 * Thrown when an operation against the database fails
 */
public class DatabaseException extends RuntimeException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
