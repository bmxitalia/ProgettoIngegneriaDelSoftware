package it.iks.openapm.alerts;

import it.iks.openapm.alerts.exceptions.BaselineNotFoundException;
import it.iks.openapm.databases.Database;
import it.iks.openapm.models.BaselineValue;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.strategies.Strategy;
import it.iks.openapm.templators.IndexTemplator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * Retriever to fetch baseline value given an evaluation time
 */
public class BaselineRetriever {
    private final BaselineValue config;
    private final Database database;
    private final IndexTemplator indexTemplator;
    private final Operator[] filters;
    private final Strategy strategy;
    private final Operator calculation;

    /**
     * Instantiate BaselineRetriever.
     *
     * @param config         Baseline retriever configuration
     * @param database       Baseline database
     * @param indexTemplator Index templator
     * @param filters        List of filters to retrieve baseline
     * @param strategy       Strategy to retrieve correct time baseline
     * @param calculation    Operator to calculate a value from baseline item
     */
    public BaselineRetriever(BaselineValue config,
                             Database database,
                             IndexTemplator indexTemplator,
                             Operator[] filters,
                             Strategy strategy,
                             Operator calculation) {
        this.config = config;
        this.database = database;
        this.indexTemplator = indexTemplator;
        this.filters = filters;
        this.strategy = strategy;
        this.calculation = calculation;
    }

    /**
     * Determine baseline value at given moment
     *
     * @param calculationTime Checking time to use to fetch appropriate baseline
     * @return Baseline value
     * @throws BaselineNotFoundException Thrown if baseline is not found
     */
    public double value(Date calculationTime) throws BaselineNotFoundException {
        Map<String, Object> baseline = fetch(calculationTime);

        if (baseline == null) {
            throw new BaselineNotFoundException(String.format(
                    "Baseline cannot be found: calculationTime=%s, strategy=%s, filters=%s",
                    calculationTime.toString(),
                    config.getStrategy(),
                    Arrays.toString(filters)
            ));
        }

        return calculation.calculate(Collections.singletonList(baseline));
    }

    private Map<String, Object> fetch(Date calculationTime) {
        Date period = strategy.previousPeriod(calculationTime);
        Map<String, Object> conditions = strategy.attributes(period);

        return findFirst(database.findNewestWhere(
                indexTemplator.value(config.getIndex(), period),
                conditions
        ));
    }

    private Map<String, Object> findFirst(Iterable<Map<String, Object>> baselines) {
        for (Map<String, Object> baseline : baselines) {
            if (match(baseline)) {
                return baseline;
            }
        }

        return null;
    }

    private boolean match(Map<String, Object> baseline) {
        for (Operator filter : filters) {
            if (!filter.match(baseline)) {
                return false;
            }
        }

        return true;
    }
}
