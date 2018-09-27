package it.iks.openapm.actions.exceptions;

/**
 * Thrown when an action configuration is not valid
 */
public class ActionConfigurationException extends ActionException {
    public ActionConfigurationException(String message) {
        super(message);
    }

    public ActionConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
