package it.iks.openapm.alerts;

import it.iks.openapm.alerts.exceptions.BaselineNotFoundException;
import it.iks.openapm.operators.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Condition element holding a configuration, a state and receiving metrics
 */
public class Condition {
    private static final Logger log = LoggerFactory.getLogger(Condition.class);

    private final MetricState[] metricStates;
    private final Operator calculation;
    private final BaselineRetriever baseline;
    private final Operator evaluation;

    /**
     * Instantiate Condition.
     *
     * @param metricStates States of metrics
     * @param calculation  Operator to merge all metrics' values into a single comparable value
     * @param baseline     Retriever for baseline value at given time
     * @param evaluation   Operator to evaluate combined metrics' value and determine condition status
     */
    public Condition(MetricState[] metricStates,
                     Operator calculation,
                     BaselineRetriever baseline,
                     Operator evaluation) {
        this.metricStates = metricStates;
        this.calculation = calculation;
        this.baseline = baseline;
        this.evaluation = evaluation;
    }

    /**
     * Evaluate an incoming metric.
     *
     * @param metric Metric to evaluate
     */
    public void evaluateMetric(Map<String, Object> metric) {
        for (MetricState state : metricStates) {
            state.evaluateMetric(metric);
        }
    }

    /**
     * Determine if condition is active (true) or not
     *
     * @return True if active, otherwise false
     */
    public boolean active() {
        Map<String, Object> item = new HashMap<>();
        item.put("value", metricsValue());

        try {
            if (baseline != null) {
                item.put("baseline_value", baseline.value(new Date()));
            }
        } catch (BaselineNotFoundException e) {
            log.debug("Baseline not found: " + baseline, e);
        }

        return evaluation.match(item);
    }

    private double metricsValue() {
        return calculation.calculate(
                Arrays.stream(metricStates)
                        .filter(Objects::nonNull)
                        .map(MetricState::getLastMetric)
                        .collect(Collectors.toList())
        );
    }
}
