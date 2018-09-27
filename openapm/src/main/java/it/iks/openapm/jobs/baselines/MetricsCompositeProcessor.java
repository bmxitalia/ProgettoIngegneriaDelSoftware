package it.iks.openapm.jobs.baselines;

import it.iks.openapm.jobs.GroupItemsFilter;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.strategies.StrategyFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.support.CompositeItemProcessor;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Define a series of processors to generate a baseline from metrics.
 *
 * This wrapper class is needed to listen correctly to BeforeStep event.
 */
public class MetricsCompositeProcessor extends CompositeItemProcessor<Iterable<Iterable<Map<String, Object>>>, Iterable<Map<String, Object>>> {
    private final OperatorFactory operatorFactory;
    private final StrategyFactory strategyFactory;

    /**
     * Instantiate MetricsCompositeProcessor.
     *
     * @param operatorFactory Operator factory
     * @param strategyFactory Strategy factory
     */
    public MetricsCompositeProcessor(OperatorFactory operatorFactory, StrategyFactory strategyFactory) {
        this.operatorFactory = operatorFactory;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Load configurations from job parameters/context into local variables
     * before step starts and set delegates processors.
     *
     * @param stepExecution StepExecution context to extract data from
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        BaselineConfig config = (BaselineConfig) stepExecution.getJobExecution().getExecutionContext().get("config");
        Date calculationTime = stepExecution.getJobParameters().getDate("calculation_time");

        setDelegates(Arrays.asList(
                new GroupItemsFilter(config, operatorFactory),
                new MetricsGroupsAggregator(config, operatorFactory),
                new BaselinesCalculator(config, calculationTime, operatorFactory, strategyFactory)
        ));
    }
}
