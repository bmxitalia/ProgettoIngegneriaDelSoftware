package it.iks.openapm.events;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

/**
 * Event representing a created metric
 */
public class MetricCreated extends ApplicationEvent {
    /**
     * Created metric
     */
    private final Map<String, Object> metric;

    /**
     * Create a new MetricCreated event.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param metric Created metric
     */
    public MetricCreated(Object source, Map<String, Object> metric) {
        super(source);
        this.metric = metric;
    }

    /**
     * Get metric
     *
     * @return Created metric
     */
    public Map<String, Object> getMetric() {
        return metric;
    }
}
