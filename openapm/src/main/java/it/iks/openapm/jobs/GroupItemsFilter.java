package it.iks.openapm.jobs;

import it.iks.openapm.models.contracts.FilterableConfig;
import it.iks.openapm.operators.Operator;
import it.iks.openapm.operators.OperatorFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Filter all input items divided into groups using provided config's filters operations
 */
public class GroupItemsFilter implements ItemProcessor<Iterable<Iterable<Map<String, Object>>>, List<List<Map<String, Object>>>> {
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
     * Initialize GroupItemsFilter.
     *
     * @param config          Configuration of filters to use
     * @param operatorFactory Operator factory
     */
    public GroupItemsFilter(FilterableConfig config, OperatorFactory operatorFactory) {
        this.config = config;
        this.operatorFactory = operatorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<List<Map<String, Object>>> process(Iterable<Iterable<Map<String, Object>>> groups) {
        filters = buildOperators();

        List<List<Map<String, Object>>> filteredGroups = new ArrayList<>();

        for (Iterable<Map<String, Object>> group : groups) {
            filteredGroups.add(processGroup(group));
        }

        return filteredGroups;
    }

    /**
     * Filter a list of items
     *
     * @param items List of items
     * @return List of items passing all filters
     */
    private List<Map<String, Object>> processGroup(Iterable<Map<String, Object>> items) {
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
