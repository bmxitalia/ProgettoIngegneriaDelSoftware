package it.iks.openapm.events;

import it.iks.openapm.models.AlertConfig;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * Event representing a triggered alert
 */
public class AlertTriggered extends ApplicationEvent {
    /**
     * Alert configuration
     */
    private final AlertConfig config;

    /**
     * When alert was triggered
     */
    private final Date when;

    /**
     * Create a new AlertTriggered event.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param config Alert configuration
     * @param when   When alert was triggered
     */
    public AlertTriggered(Object source, AlertConfig config, Date when) {
        super(source);
        this.config = config;
        this.when = when;
    }

    /**
     * Get triggered alert configuration
     *
     * @return Alert configuration
     */
    public AlertConfig getConfig() {
        return config;
    }

    /**
     * Get when the alert was triggered
     *
     * @return Triggered date
     */
    public Date getWhen() {
        return when;
    }
}
