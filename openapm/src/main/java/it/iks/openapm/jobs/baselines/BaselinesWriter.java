package it.iks.openapm.jobs.baselines;

import it.iks.openapm.databases.Database;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

/**
 * ItemWriter to save generated baselines
 */
public class BaselinesWriter extends BaselineStepComponent implements ItemWriter<Iterable<Map<String, Object>>> {
    /**
     * Baselines database implementation
     */
    private final Database database;

    /**
     * Templator to process index name
     */
    private final IndexTemplator templator;

    /**
     * Baselines type for database which needs it
     */
    private final String baselinesType;

    /**
     * Instantiate BaselinesWriter
     *
     * @param database      Baselines database implementation
     * @param templator     Templator to process index name
     * @param baselinesType Baselines type for database which needs it
     */
    public BaselinesWriter(Database database, IndexTemplator templator, String baselinesType) {
        this.database = database;
        this.templator = templator;
        this.baselinesType = baselinesType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends Iterable<Map<String, Object>>> items) {
        for (Iterable<Map<String, Object>> baselines : items) {
            for (Map<String, Object> baseline : baselines) {
                database.insert(
                        templator.value(config.getBaselinesIndex(), calculationTime),
                        baseline,
                        baselinesType
                );
            }
        }
    }
}
