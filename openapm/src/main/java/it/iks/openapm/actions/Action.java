package it.iks.openapm.actions;

import it.iks.openapm.events.AlertTriggered;

/**
 * An implementation of a remediation action
 */
@FunctionalInterface
public interface Action {
    /**
     * Execute remediation action for given alert
     *
     * @param event Alert triggered event
     */
    void run(AlertTriggered event);
}
