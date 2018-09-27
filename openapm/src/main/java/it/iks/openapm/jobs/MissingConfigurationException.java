package it.iks.openapm.jobs;

/**
 * Thrown when a job configuration can not be retrieved from database
 */
public class MissingConfigurationException extends RuntimeException {
    public MissingConfigurationException(String message) {
        super(message);
    }
}
