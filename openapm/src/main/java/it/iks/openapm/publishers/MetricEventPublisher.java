package it.iks.openapm.publishers;

import it.iks.openapm.events.MetricCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

/**
 * Publisher for metric-related events
 */
@Component
public class MetricEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public MetricEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Fire a metric created event
     *
     * @param metric Created Metric
     */
    public void created(Map<String, Object> metric) {
        // Event listeners can (and should) be async. Since no intentional update of a metric
        // is accepted by the design, we send an UnmodifiableMap, to avoid accidental
        // not thread-safe updates.
        applicationEventPublisher.publishEvent(new MetricCreated(this, Collections.unmodifiableMap(metric)));
    }
}
