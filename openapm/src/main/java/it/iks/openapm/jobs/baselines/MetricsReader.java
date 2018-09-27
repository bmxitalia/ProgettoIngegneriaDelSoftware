package it.iks.openapm.jobs.baselines;

import it.iks.openapm.databases.Database;
import it.iks.openapm.strategies.Strategy;
import it.iks.openapm.strategies.StrategyFactory;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.batch.item.ItemReader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ItemReader to read metrics in periods from database using current job BaselineConfig.
 *
 * It uses Strategy to determine periods, then it fetches every metric grouped by period.
 *
 * Note: ItemReader is not the best syntax to use here since we need to work
 * using streams on the full set of metrics and not one-by-one. Although we use
 * it to keep the read-process-write paradigm and re-use Spring Batch structure.
 * We might consider switch to a Tasklet in the future.
 */
public class MetricsReader extends BaselineStepComponent implements ItemReader<Iterable<Iterable<Map<String, Object>>>> {
    private static final int MILLISECOND_IN_AN_HOUR = 3_600_000;

    /**
     * Metrics database implementation
     */
    private final Database database;

    /**
     * Templator to process index name
     */
    private final IndexTemplator templator;

    /**
     * Strategy factory
     */
    private final StrategyFactory strategyFactory;

    /**
     * Flag to avoid multiple returns of same traces
     */
    private boolean returned = false;

    /**
     * Instantiate MetricsReader
     *
     * @param database        Metrics database implementation
     * @param templator       Templator to process index name
     * @param strategyFactory Strategy factory
     */
    public MetricsReader(Database database, IndexTemplator templator, StrategyFactory strategyFactory) {
        this.database = database;
        this.templator = templator;
        this.strategyFactory = strategyFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<Iterable<Map<String, Object>>> read() {
        if (returned) {

            // We need to restore the default state of the reader
            // since Spring Batch will be reusing this same instance.
            // TODO: Make new instance of ItemReader on every step execution
            returned = false;

            return null;
        }

        Strategy strategy = strategyFactory.byIdentifier(config.getStrategy());
        List<Iterable<Map<String, Object>>> metricsGroups = new ArrayList<>();

        for (Date period : strategy.periods(calculationTime)) {
            metricsGroups.add(fetch(period));
        }

        returned = true;

        return metricsGroups;
    }

    /**
     * Fetch a period of metrics.
     *
     * Period is identified by starting time, and it'll contain
     * all metrics from starting time to starting time plus 1 hour.
     *
     * @param period Starting time for period
     * @return List of metrics
     */
    private Iterable<Map<String, Object>> fetch(Date period) {
        return database.findAllInInterval(
                templator.value(config.getMetricsIndex(), period),
                period,
                new Date(period.getTime() + MILLISECOND_IN_AN_HOUR)
        );
    }
}
