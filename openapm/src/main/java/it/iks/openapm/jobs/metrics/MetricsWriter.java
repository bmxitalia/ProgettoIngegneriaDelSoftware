package it.iks.openapm.jobs.metrics;

import it.iks.openapm.databases.Database;
import it.iks.openapm.publishers.MetricEventPublisher;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;

/**
 * ItemWriter to save generated metrics
 */
public class MetricsWriter extends MetricStepComponent implements ItemWriter<Iterable<Map<String, Object>>> {
    /**
     * Metrics database implementation
     */
    private final Database database;

    /**
     * Templator to process index name
     */
    private final IndexTemplator templator;

    /**
     * Metric events publisher
     */
    private final MetricEventPublisher publisher;

    /**
     * Metrics type for database which needs it
     */
    private String metricsType;

    /**
     * Instantiate MetricsWriter
     *
     * @param database    Metric database implementation
     * @param templator   Templator to process index name
     * @param publisher   Metric events publisher
     * @param metricsType Metrics type for database which needs it
     */
    public MetricsWriter(Database database, IndexTemplator templator, MetricEventPublisher publisher, String metricsType) {
        this.database = database;
        this.templator = templator;
        this.publisher = publisher;
        this.metricsType = metricsType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends Iterable<Map<String, Object>>> items) {
        for (Iterable<Map<String, Object>> metrics : items) {
            for (Map<String, Object> metric : metrics) {
                publisher.created(metric);
                database.insert(
                        templator.value(config.getMetricsIndex(), startupTime),
                        metric,
                        metricsType
                );
            }
        }
    }
}
