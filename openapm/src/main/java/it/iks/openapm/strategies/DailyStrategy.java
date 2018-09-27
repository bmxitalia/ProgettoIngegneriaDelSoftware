package it.iks.openapm.strategies;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Daily baseline strategy.
 *
 * Build a baseline for X hour will take the last dataPoints days at X hour.
 *
 * Example:
 * dataPoints = 4
 * calculation time = 2017-01-10 15.00/16.00
 * Periods:
 * - 2017-01-07 15.00/16.00
 * - 2017-01-08 15.00/16.00
 * - 2017-01-09 15.00/16.00
 * - 2017-01-10 15.00/16.00
 *
 * If calculation hits a Daylight Saving Time Changes, the time will be regulated
 * accordingly to keep unix timestamp coherent. This means a +/- 1 hour can be seen
 * when reading comparing times.
 */
public class DailyStrategy extends AbstractStrategy {
    public static final String IDENTIFIER = "daily";

    /**
     * Initialize DailyStrategy.
     *
     * @param dataPoints Number of data points to retrieve on every strategy.
     */
    public DailyStrategy(int dataPoints) {
        super(dataPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Date> periods(Date calculationTime) {
        List<Date> periods = new ArrayList<>();
        periods.add(calculationTime);

        Instant lastTime = calculationTime.toInstant();
        for (int i = 0; i < dataPoints - 1; i++) {
            lastTime = lastTime.minus(1, ChronoUnit.DAYS);
            periods.add(0, Date.from(lastTime));
        }

        return periods;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> attributes(Date calculationTime) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("strategy", IDENTIFIER);
        attributes.put("hour", ZonedDateTime.ofInstant(calculationTime.toInstant(), ZoneId.systemDefault()).getHour());
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date previousPeriod(Date calculationTime) {
        return Date.from(calculationTime.toInstant().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS));
    }
}
