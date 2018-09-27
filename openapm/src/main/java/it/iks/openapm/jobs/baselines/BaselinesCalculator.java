package it.iks.openapm.jobs.baselines;

import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.strategies.StrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;

/**
 * Calculate a baseline for every group of metrics in input.
 *
 * Every groups should be composed of a set of periods, containing multiple metrics.
 * Output will be both the mean of this metrics and the standard deviation.
 * Given X groups o Y periods, there'll be X baselines.
 *
 * {@see https://docops.ca.com/ca-performance-management/3-1/en/using/performance-metrics/baseline-calculations}
 */
public class BaselinesCalculator implements ItemProcessor<Map<String, List<Iterable<Map<String, Object>>>>, Iterable<Map<String, Object>>> {
    private static final Logger log = LoggerFactory.getLogger(BaselinesCalculator.class);

    /**
     * Config model for calculation
     */
    private final BaselineConfig config;

    /**
     * Calculation time of baselines
     */
    private final Date calculationTime;

    /**
     * Operator Factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * Strategy Factory
     */
    private final StrategyFactory strategyFactory;

    /**
     * Initialize BaselinesCalculator
     *
     * @param config          Config model for calculation
     * @param calculationTime Calculation time of baselines
     * @param operatorFactory Operator Factory
     * @param strategyFactory Strategy Factory
     */
    public BaselinesCalculator(BaselineConfig config, Date calculationTime, OperatorFactory operatorFactory, StrategyFactory strategyFactory) {
        this.config = config;
        this.calculationTime = calculationTime;
        this.operatorFactory = operatorFactory;
        this.strategyFactory = strategyFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Map<String, Object>> process(Map<String, List<Iterable<Map<String, Object>>>> groups) {
        List<Map<String, Object>> baselines = new ArrayList<>();
        Map<String, Object> attributes = strategyFactory.byIdentifier(config.getStrategy()).attributes(calculationTime);

        for (String key : groups.keySet()) {
            try {
                baselines.add(calculate(key, groups.get(key), attributes));
            } catch (ArithmeticException e) {
                log.error("Cannot calculate baseline", e);
            }
        }

        return baselines;
    }

    /**
     * Create a baseline from given information
     *
     * @param label      Group label to identify an aggregated baseline
     * @param periods    List of periods containing a group o metrics each
     * @param attributes Map of extra attributes to append to final baseline
     * @return Baseline element as a map
     */
    private Map<String, Object> calculate(String label, List<Iterable<Map<String, Object>>> periods, Map<String, Object> attributes) {
        Operator averageOperator = operatorFactory.average(config.getCalculationField());
        double[] means = calculateMeans(periods, averageOperator);
        double mean = calculateMean(means);

        Map<String, Object> baseline = new HashMap<>();

        baseline.put("name", config.getName());
        baseline.put("group", label);
        baseline.put("mean", mean);
        baseline.put("deviation", calculateStandardDeviation(mean, means));
        baseline.putAll(attributes);

        return baseline;
    }

    /**
     * Calculate average of metrics in each period
     *
     * @param periods         List of periods containing a group o metrics each
     * @param averageOperator Operator to use to calculate average
     * @return List of averages
     */
    private double[] calculateMeans(List<Iterable<Map<String, Object>>> periods, Operator averageOperator) {
        double[] means = new double[periods.size()];

        for (int i = 0; i < periods.size(); i++) {
            means[i] = averageOperator.calculate(periods.get(i));
        }

        return means;
    }

    /**
     * Calculate average of given values
     *
     * @param points List of values
     * @return Average
     */
    private double calculateMean(double[] points) {
        OptionalDouble average = Arrays.stream(points).average();

        if (!average.isPresent()) {
            throw new ArithmeticException("Cannot determine mean of " + Arrays.toString(points));
        }

        return average.getAsDouble();
    }

    /**
     * Calculate standard deviation of given values
     *
     * Average is accepted instead of calculated since it's
     * most likely already calculated, so we avoid the repetition.
     *
     * @param mean   The average of all values
     * @param points List of values
     * @return Standard Deviation
     */
    private double calculateStandardDeviation(double mean, double[] points) {
        double sum = 0;
        for (double point : points) {
            sum += Math.pow(point - mean, 2);
        }

        return Math.sqrt(sum / points.length);
    }
}
