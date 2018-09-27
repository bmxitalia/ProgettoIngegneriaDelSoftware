package it.iks.openapm.jobs;

import it.iks.openapm.models.Operation;
import it.iks.openapm.models.contracts.AggregableConfig;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Aggregate all items into groups defined by "aggregator" operation in config
 */
public class ItemsAggregator implements ItemProcessor<Iterable<Map<String, Object>>, Map<String, List<Map<String, Object>>>> {
    /**
     * Configuration of aggregator to use
     */
    private final AggregableConfig config;

    /**
     * Operator factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * Initialize ItemsAggregator.
     *
     * @param config          Configuration of aggregator to use
     * @param operatorFactory Operator factory
     */
    public ItemsAggregator(AggregableConfig config, OperatorFactory operatorFactory) {
        this.config = config;
        this.operatorFactory = operatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<Map<String, Object>>> process(Iterable<Map<String, Object>> items) {
        Operator aggregator = aggregator();

        return StreamSupport.stream(items.spliterator(), false)
                .collect(Collectors.groupingBy(aggregator::group));
    }

    /**
     * Build the aggregator operator.
     *
     * If no aggregation is specified, a NullOperator is returned
     * so that every trace is places in an "all" group.
     *
     * @return Aggregator operator
     */
    private Operator aggregator() {
        Operation operation = config.getAggregation();

        if (operation == null) {
            return operatorFactory.nullOperator();
        }

        return operatorFactory.byModel(config.getAggregation());
    }
}
