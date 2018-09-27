package it.iks.openapm.alerts;

import it.iks.openapm.operators.Operator;

import java.util.Map;

/**
 * Condition's metric state, holding the last metric and capable of filtering incoming metrics
 */
public class MetricState {
    /**
     * List of filters to retrieve a metric
     */
    private final Operator[] filters;

    /**
     * Last received metric
     *
     * Variable is marked as volatile to be consistent on multiple
     * reads from different threads trying to evaluate alert state.
     */
    volatile private Map<String, Object> lastMetric;

    /**
     * Instantiate MetricState.
     *
     * @param filters List of filters to retrieve a metric
     */
    public MetricState(Operator[] filters) {
        this.filters = filters;
    }

    /**
     * Get last received metric
     *
     * @return Last metric
     */
    public Map<String, Object> getLastMetric() {
        return lastMetric;
    }

    /**
     * Evaluate an incoming metric to see if it passes the filters
     * and eventually save it as last metric.
     *
     * @param metric Metric to evaluate
     */
    public void evaluateMetric(Map<String, Object> metric) {
        if (passes(metric)) {
            lastMetric = metric;
        }
    }

    /**
     * Check if metric passes all filters
     *
     * @param metric Metric to evaluate
     * @return True if it passes
     */
    private boolean passes(Map<String, Object> metric) {
        for (Operator filter : filters) {
            if (!filter.match(metric)) {
                return false;
            }
        }

        return true;
    }
}
