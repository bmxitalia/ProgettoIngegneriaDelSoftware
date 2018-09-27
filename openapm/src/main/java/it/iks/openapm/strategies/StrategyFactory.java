package it.iks.openapm.strategies;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to alert Strategy objects.
 */
@Component
public class StrategyFactory {
    private static final Logger log = LoggerFactory.getLogger(StrategyFactory.class);

    /**
     * Number of data points to retrieve on every strategy.
     */
    @Value("${app.baselines.data_points}")
    private int dataPoints;

    /**
     * Table mapping strategy field to concrete strategy class
     */
    private static final Map<String, Class<? extends Strategy>> strategiesTable;

    static {
        Map<String, Class<? extends Strategy>> tempStrategiesTable = new HashMap<>();
        tempStrategiesTable.put(DailyStrategy.IDENTIFIER, DailyStrategy.class);
        tempStrategiesTable.put(WeeklyStrategy.IDENTIFIER, WeeklyStrategy.class);
        tempStrategiesTable.put(MonthlyStrategy.IDENTIFIER, MonthlyStrategy.class);
        strategiesTable = Collections.unmodifiableMap(tempStrategiesTable);
    }

    /**
     * Build strategy using its identifier
     *
     * @param identifier Strategy identifier
     * @return Concrete Strategy
     */
    public Strategy byIdentifier(String identifier) {
        if (!strategiesTable.containsKey(identifier)) {
            throw new MissingStrategyException(String.format("Cannot find strategy \"%s\"", identifier));
        }

        try {
            Class<? extends Strategy> strategyClass = strategiesTable.get(identifier);

            log.debug("Concrete strategy: " + strategyClass.toString());

            Constructor<? extends Strategy> constructor = strategyClass.getConstructor(int.class);

            return constructor.newInstance(dataPoints);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // This should never happen since we are using a reference table
            throw new MissingStrategyException(String.format("Cannot instantiate strategy \"%s\"", identifier), e);
        }
    }
}
