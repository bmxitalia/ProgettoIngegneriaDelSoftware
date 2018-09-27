package it.iks.openapm.alerts;

import it.iks.openapm.alerts.factories.AlertFactory;
import it.iks.openapm.events.MetricCreated;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.repositories.AlertConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manage alerts configuration/states and listen to metric creation events
 */
@Component
public class AlertsManager implements ApplicationListener<MetricCreated> {
    private static final Logger log = LoggerFactory.getLogger(AlertsManager.class);

    private final AlertConfigRepository alertRepository;
    private final AlertFactory alertFactory;

    /**
     * Active alerts to evaluate on metric creation
     */
    private List<Alert> alerts;

    @Autowired
    public AlertsManager(AlertConfigRepository alertRepository, AlertFactory alertFactory) {
        this.alertRepository = alertRepository;
        this.alertFactory = alertFactory;
    }

    @PostConstruct
    public void postConstructor() {
        List<Alert> alerts = new ArrayList<>();

        for (AlertConfig config : alertRepository.findAll()) {
            try {
                alerts.add(alertFactory.alert(config));
            } catch (Exception e) {
                log.warn(String.format(
                        "Exception when creating alert \"%s\"",
                        config != null ? config.getId() : "null"
                ), e);
            }
        }

        this.alerts = Collections.unmodifiableList(alerts);
    }

    /**
     * {@inheritDoc}
     */
    @Async
    @Override
    public void onApplicationEvent(MetricCreated event) {
        alerts.forEach(alert -> alert.evaluateMetric(event.getMetric()));
    }
}
