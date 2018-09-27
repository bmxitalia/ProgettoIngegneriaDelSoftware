package it.iks.openapm.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;
import it.iks.openapm.models.AlertConfig;

/**
 * Verifier that fire remediation actions as soon as the state changes to {@code AlertState.ACTIVE}
 */
public class ImmediateVerifier extends AbstractVerifier {
    /**
     * Instantiate ImmediateVerifier.
     *
     * @param config Alert configuration
     */
    public ImmediateVerifier(AlertConfig config) {
        super(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChange(AlertState newState, AlertState oldState) {
        if (newState.equals(AlertState.ACTIVE)) {
            publisher.triggered(config);
        }
    }
}
