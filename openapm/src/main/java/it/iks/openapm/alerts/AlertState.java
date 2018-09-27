package it.iks.openapm.alerts;

/**
 * Enum representing all possible states of an Alert
 */
public enum AlertState {
    /**
     * Alert is evaluating to true
     */
    ACTIVE,

    /**
     * Alert is evaluating to false
     */
    INACTIVE;
}
