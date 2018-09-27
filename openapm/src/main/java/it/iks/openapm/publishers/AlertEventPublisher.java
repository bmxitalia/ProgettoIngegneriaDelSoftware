package it.iks.openapm.publishers;

import it.iks.openapm.events.AlertTriggered;
import it.iks.openapm.models.AlertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Publisher for alert-related events
 */
@Component
public class AlertEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public AlertEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Fire an alert triggered event
     *
     * @param config Triggered alert configuration
     */
    public void triggered(AlertConfig config) {
        applicationEventPublisher.publishEvent(new AlertTriggered(this, config, new Date()));
    }
}
