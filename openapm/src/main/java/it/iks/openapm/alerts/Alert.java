package it.iks.openapm.alerts;

import it.iks.openapm.alerts.evaluators.Evaluator;
import it.iks.openapm.alerts.verifiers.Verifier;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.utils.Debouncer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Alert element holding a configuration, a state and receiving metrics
 */
public class Alert {
    private static final Logger log = LoggerFactory.getLogger(Alert.class);

    private final Debouncer debouncer;
    private final int evaluateWait;

    private final AlertConfig config;
    private final List<Condition> conditions;
    private final Evaluator evaluator;
    private final Verifier verifier;

    private AtomicReference<AlertState> currentStatus = new AtomicReference<>(AlertState.INACTIVE);

    /**
     * Instantiate Alert.
     *
     * @param config       Alert configuration
     * @param conditions   List of conditions
     * @param evaluator    Evaluator to determine global state from condition's state
     * @param verifier     Verifier to determine when to fire event
     * @param debouncer    Debouncer to avoid excessive evaluations
     * @param evaluateWait Debounce time for evaluations
     */
    public Alert(AlertConfig config,
                 List<Condition> conditions,
                 Evaluator evaluator,
                 Verifier verifier,
                 Debouncer debouncer,
                 int evaluateWait) {
        this.config = config;
        this.conditions = conditions;
        this.evaluator = evaluator;
        this.verifier = verifier;
        this.debouncer = debouncer;
        this.evaluateWait = evaluateWait;
    }

    /**
     * Evaluate given metric and update internal state
     *
     * @param metric New metric
     */
    public void evaluateMetric(Map<String, Object> metric) {
        conditions.forEach(condition -> condition.evaluateMetric(metric));

        updateStatus();
    }

    private void updateStatus() {
        debouncer.call(this, () -> {
            AlertState newState = newStatus();
            log.debug(String.format("Alert \"%s\" status=%s", config.getId(), newState.name()));

            AlertState previousStatus = currentStatus.getAndSet(newState);

            if (!newState.equals(previousStatus)) {
                log.info(String.format("Alert \"%s\" status changed to %s", config.getId(), newState.name()));

                verifier.onChange(newState, previousStatus);
            }
        }, evaluateWait);
    }

    private synchronized AlertState newStatus() {
        return evaluator.evaluate(conditions) ? AlertState.ACTIVE : AlertState.INACTIVE;
    }
}
