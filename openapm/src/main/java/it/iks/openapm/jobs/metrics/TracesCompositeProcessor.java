package it.iks.openapm.jobs.metrics;

import it.iks.openapm.jobs.ItemsAggregator;
import it.iks.openapm.jobs.ItemsFilter;
import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Trace composite processor defines a series of processors to generate a metric from traces.
 *
 * This wrapper class is needed to listen correctly to BeforeStep event.
 */
public class TracesCompositeProcessor extends CompositeItemProcessor<Iterable<Map<String, Object>>, Iterable<Map<String, Object>>> {
    private final OperatorFactory operatorFactory;

    /**
     * Initialize TracesCompositeProcessor.
     *
     * @param operatorFactory Operator factory
     */
    public TracesCompositeProcessor(OperatorFactory operatorFactory) {
        this.operatorFactory = operatorFactory;
    }

    /**
     * Load configurations from job parameters/context into local variables
     * before step starts and set delegates processors.
     *
     * @param stepExecution StepExecution context to extract data from
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        MetricConfig config = (MetricConfig) stepExecution.getJobExecution().getExecutionContext().get("config");
        Date startupTime = stepExecution.getJobParameters().getDate("startup_time");

        setDelegates(Arrays.asList(
                new ItemsFilter(config, operatorFactory),
                new ItemsAggregator(config, operatorFactory),
                new MetricsCalculator(config, startupTime, operatorFactory)
        ));
    }
}
