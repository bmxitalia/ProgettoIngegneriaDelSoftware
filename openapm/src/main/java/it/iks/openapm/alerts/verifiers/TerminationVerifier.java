package it.iks.openapm.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;
import it.iks.openapm.models.AlertConfig;

/**
 * Verifier that fire remediation actions as soon as the state changes to {@code AlertState.INACTIVE}
 */
public class TerminationVerifier extends AbstractVerifier {
    /**
     * Instantiate TerminationVerifier.
     *
     * @param config Alert configuration
     */
    public TerminationVerifier(AlertConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChange(AlertState newState, AlertState oldState) {
        if (newState.equals(AlertState.INACTIVE)) {
            publisher.triggered(config);
        }
    }
}
