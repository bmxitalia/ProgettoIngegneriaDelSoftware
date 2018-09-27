package it.iks.openapm.strategies;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Monthly baseline strategy.
 *
 * Build a baseline for X hour at day of month Y will take the last dataPoints days of month Y at X hour.
 *
 * Example:
 * dataPoints = 4
 * calculation time = 2018-06-15 15.00/16.00
 * Periods:
 * - 2018-03-15 15.00/16.00
 * - 2018-04-15 15.00/16.00
 * - 2018-05-15 15.00/16.00
 * - 2018-06-15 15.00/16.00
 *
 * If some periods doesn't have needed day number, it will be skipped.
 *
 * Example:
 * dataPoints = 4
 * calculation time = 2018-08-31 15.00/16.00
 * Periods:
 * - 2018-03-31 15.00/16.00
 * - 2018-05-31 15.00/16.00
 * - 2018-07-31 15.00/16.00
 * - 2018-08-31 15.00/16.00
 *
 * If calculation hits a Daylight Saving Time Changes, the time will be regulated
 * accordingly to keep unix timestamp coherent. This means a +/- 1 hour can be seen
 * when reading comparing times.
 */
public class MonthlyStrategy extends AbstractStrategy {
    public static final String IDENTIFIER = "monthly";

    /**
     * Initialize MonthlyStrategy.
     *
     * @param dataPoints Number of data points to retrieve on every strategy.
     */
    public MonthlyStrategy(int dataPoints) {
        super(dataPoints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Date> periods(Date calculationTime) {
        List<Date> periods = new ArrayList<>();
        periods.add(calculationTime);

        ZonedDateTime lastTime = ZonedDateTime.ofInstant(
                calculationTime.toInstant(),
                ZoneId.systemDefault()
        );

        while (periods.size() < dataPoints) {
            lastTime = previousMonth(lastTime);
            periods.add(0, Date.from(lastTime.toInstant()));
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
        attributes.put("day_of_month", datetime.getDayOfMonth());
        return attributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date previousPeriod(Date calculationTime) {
        return Date.from(
                previousMonth(ZonedDateTime.ofInstant(
                        calculationTime.toInstant(),
                        ZoneId.systemDefault()
                ))
                        .toInstant()
                        .truncatedTo(ChronoUnit.HOURS)
        );
    }

    private ZonedDateTime previousMonth(ZonedDateTime lastTime) {
        if (lastTime.minusMonths(1).getDayOfMonth() == lastTime.getDayOfMonth()) {
            return lastTime.minusMonths(1);
        } else {
            return lastTime.minusMonths(2);
        }
    }
}
