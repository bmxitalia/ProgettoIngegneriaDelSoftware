package it.iks.openapm.jobs.metrics;

import it.iks.openapm.databases.Database;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.batch.item.ItemReader;

import java.util.Date;
import java.util.Map;

/**
 * ItemReader to read traces from database using current job MetricConfig.
 *
 * Note: ItemReader is not the best syntax to use here since we need to work
 * using streams on the full set of traces and not one-by-one. Although we use
 * it to keep the read-process-write paradigm and re-use Spring Batch structure.
 * We might consider switch to a Tasklet in the future.
 */
public class TracesReader extends MetricStepComponent implements ItemReader<Iterable<Map<String, Object>>> {
    /**
     * Traces database implementation
     */
    private final Database database;

    /**
     * Templator to process index name
     */
    private final IndexTemplator templator;

    /**
     * Flag to avoid multiple returns of same traces
     */
    private boolean returned = false;

    /**
     * Instantiate TracesReader
     *
     * @param database  Traces database implementation
     * @param templator Templator to process index name
     */
    public TracesReader(Database database, IndexTemplator templator) {
        this.database = database;
        this.templator = templator;
    }

    /**
     * Fetch traces from database and return them as Iterable.
     *
     * @return Iterable with traces
     */
    @Override
    public Iterable<Map<String, Object>> read() {
        if (returned) {

            // We need to restore the default state of the reader
            // since Spring Batch will be reusing this same instance.
            // TODO: Make new instance of ItemReader on every step execution
            returned = false;

            return null;
        }

        Iterable<Map<String, Object>> objects = fetch();

        returned = true;

        return objects;
    }

    /**
     * Fetch all traces created in the last "duration" seconds
     *
     * @return List of traces
     */
    private Iterable<Map<String, Object>> fetch() {
        return database.findAllInInterval(
                templator.value(config.getTracesIndex(), startupTime),
                new Date(startupTime.getTime() - (config.getDuration() * 1000)),
                startupTime
        );
    }
}
