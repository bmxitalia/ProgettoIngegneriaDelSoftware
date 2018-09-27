package it.iks.openapm.strategies;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Abstract baseline calculation strategy.
 *
 * It holds the logic to determine time periods to fetch to generate
 * a baseline.
 */
public interface Strategy {
    /**
     * Determine all fetch periods for give calculation time.
     *
     * @param calculationTime Calculation time of baseline, must be last period
     * @return List of periods to fetch metrics, represented by the left limit (beginning time).
     */
    List<Date> periods(Date calculationTime);

    /**
     * Set of attributes to identify calculation period of baseline.
     *
     * @param calculationTime Calculation time of baseline
     * @return Attributes map
     */
    Map<String, Object> attributes(Date calculationTime);

    /**
     * Determine a date for retrieving a baseline to compare to a metric of given calculation time.
     *
     * @param calculationTime Calculation time of metric
     * @return Fetching period
     */
    Date previousPeriod(Date calculationTime);
}
