package it.iks.openapm.strategies;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Weekly baseline strategy.
 *
 * Build a baseline for X hour at day of week Y will take the last dataPoints days of week Y at X hour.
 *
 * Example:
 * dataPoints = 4
 * calculation time = Friday 2018-06-01 15.00/16.00
 * Periods:
 * - Friday 2018-05-1 15.00/16.00
 * - Friday 2018-05-18 15.00/16.00
 * - Friday 2018-05-25 15.00/16.00
 * - Friday 2018-06-01 15.00/16.
 *
 * If calculation hits a Daylight Saving Time Changes, the time will be regulated
 * accordingly to keep unix timestamp coherent. This means a +/- 1 hour can be seen
 * when reading comparing times.
 */
public class WeeklyStrategy extends AbstractStrategy {
    public static final String IDENTIFIER = "weekly";

    /**
     * Initialize WeeklyStrategy.
     *
     * @param dataPoints Number of data points to retrieve on every strategy.
     */
    public WeeklyStrategy(int dataPoints) {
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
            lastTime = lastTime.minus(7, ChronoUnit.DAYS);
            periods.add(0, Date.from(lastTime));
        }

        return periods;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> attributes(Date calculationTime) {
        ZonedDateTime datetime = ZonedDateTime.ofInstant(calculationTime.toInstant(), ZoneId.systemDefault());

        Map<String, Object> attributes = new HashMap<>();
        attributes.put("strategy", IDENTIFIER);
        attributes.put("hour", datetime.getHour());
        attributes.put("day_of_week", datetime.getDayOfWeek().getValue());
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date previousPeriod(Date calculationTime) {
        return Date.from(calculationTime.toInstant().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS));
    }
}
