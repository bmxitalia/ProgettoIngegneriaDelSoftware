package it.iks.openapm.models.contracts;

import it.iks.openapm.models.Operation;

/**
 * A configuration model that allows elements to be aggregated using a given Operation
 */
public interface AggregableConfig {
    /**
     * Get operation used to aggregate items.
     *
     * @return Aggregation operation
     */
    Operation getAggregation();
}
