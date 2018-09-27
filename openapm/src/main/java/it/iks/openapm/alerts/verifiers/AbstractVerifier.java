package it.iks.openapm.alerts.verifiers;

import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.publishers.AlertEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base verifier implementation
 */
abstract public class AbstractVerifier implements Verifier {
    /**
     * Alert configuration
     */
    protected final AlertConfig config;

    /**
     * Alert-related event publisher
     */
    protected AlertEventPublisher publisher;

    /**
     * Instantiate AbstractVerifier.
     *
     * @param config Alert configuration
     */
    public AbstractVerifier(AlertConfig config) {
        this.config = config;
    }

    /**
     * Autowire alert-related event publisher
     *
     * @param publisher Alert-related event publisher
     */
    @Autowired
    public void setPublisher(AlertEventPublisher publisher) {
        this.publisher = publisher;
    }
}
