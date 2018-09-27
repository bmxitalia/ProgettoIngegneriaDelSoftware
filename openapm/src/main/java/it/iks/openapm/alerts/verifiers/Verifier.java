package it.iks.openapm.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;

/**
 * An object that receives changes in state for an alert and decide
 * if/when it should fire remediation actions.
 */
@FunctionalInterface
public interface Verifier {
    /**
     * Called on every state change of an alert
     *
     * @param newState New alert state
     * @param oldState Old alert state
     */
    void onChange(AlertState newState, AlertState oldState);
}
