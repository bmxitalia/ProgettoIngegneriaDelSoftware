package it.iks.openapm.models.contracts;

import it.iks.openapm.models.Operation;

/**
 * A configuration model that allows elements to be filtered using a set of Operations
 */
public interface FilterableConfig {
    /**
     * Get operations used to filter items.
     *
     * @return Filter operation
     */
    Operation[] getFilters();
}
