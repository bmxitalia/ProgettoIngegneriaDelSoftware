package it.iks.openapm.jobs.metrics;

import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.models.Operation;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;

/**
 * Calculate metric from every group of traces in input
 */
public class MetricsCalculator implements ItemProcessor<Map<String, Iterable<Map<String, Object>>>, Iterable<Map<String, Object>>> {
    /**
     * Configuration model
     */
    private final MetricConfig config;

    /**
     * Operator factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * Job startup time
     */
    private final Date startupTime;

    /**
     * Initialize MetricsCalculator.
     *
     * @param config          Configuration model
     * @param startupTime     Job startup time
     * @param operatorFactory Operator factory
     */
    public MetricsCalculator(MetricConfig config, Date startupTime, OperatorFactory operatorFactory) {
        this.config = config;
        this.operatorFactory = operatorFactory;
        this.startupTime = startupTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Map<String, Object>> process(Map<String, Iterable<Map<String, Object>>> groups) {
        List<Map<String, Object>> metrics = new ArrayList<>();
        Operator calculator = calculator();

        groups.forEach((label, traces) -> metrics.add(
                build(label, calculator.calculate(traces))
        ));

        return metrics;
    }

    /**
     * Build the calculator operator.
     *
     * If no calculator is specified, a NullOperator is returned
     * so resulting metric will have "0" as value.
     *
     * @return Calculator operator
     */
    private Operator calculator() {
        Operation operation = config.getCalculation();

        if (operation == null) {
            return operatorFactory.nullOperator();
        }

        return operatorFactory.byModel(config.getCalculation());
    }

    /**
     * Build metric entry from label and value
     *
     * @param label Group Label
     * @param value Metric Value
     * @return Metric fields
     */
    private Map<String, Object> build(String label, double value) {
        Map<String, Object> metric = new HashMap<>();

        metric.put("name", config.getName());
        metric.put("group", label);
        metric.put("value", value);
        metric.put("startup_time", startupTime);

        return metric;
    }
}
