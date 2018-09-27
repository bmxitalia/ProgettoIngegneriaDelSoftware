package it.iks.openapm.jobs;

import it.iks.openapm.models.contracts.FilterableConfig;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Filter all input items using provided config's filters operations
 */
public class ItemsFilter implements ItemProcessor<Iterable<Map<String, Object>>, List<Map<String, Object>>> {
    /**
     * Configuration of filters to use
     */
    private final FilterableConfig config;

    /**
     * Operator factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * List of filters built from config
     */
    private Operator[] filters;

    /**
     * Initialize ItemsFilter.
     *
     * @param config          Configuration of filters to use
     * @param operatorFactory Operator factory
     */
    public ItemsFilter(FilterableConfig config, OperatorFactory operatorFactory) {
        this.config = config;
        this.operatorFactory = operatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Map<String, Object>> process(Iterable<Map<String, Object>> items) {
        filters = buildOperators();

        return StreamSupport.stream(items.spliterator(), false)
                .filter(this::matchAll)
                .collect(Collectors.toList());
    }

    /**
     * Build filter operators from config model
     *
     * @return Filter Operators
     */
    private Operator[] buildOperators() {
        if (config.getFilters() == null || config.getFilters().length == 0) {
            return new Operator[0];
        }

        Operator[] operators = new Operator[config.getFilters().length];

        for (int i = 0; i < config.getFilters().length; i++) {
            operators[i] = operatorFactory.byModel(config.getFilters()[i]);
        }

        return operators;
    }

    /**
     * Check if item matches all filters
     *
     * @param item Item to test
     * @return True if it matches
     */
    private boolean matchAll(Map<String, Object> item) {
        for (Operator filter : filters) {
            if (!filter.match(item)) {
                return false;
            }
        }

        return true;
    }
}
