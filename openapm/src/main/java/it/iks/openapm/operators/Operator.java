package it.iks.openapm.operators;

import java.util.Map;

/**
 * A concrete operator capable of working on items
 */
public interface Operator {
    /**
     * Determine if current operator is valid.
     *
     * Mainly it should check if the number of operands
     * is as expected.
     *
     * @return True if it's valid
     */
    boolean valid();

    /**
     * Determine if given item match current operator.
     *
     * @param item Item to test
     * @return True if it match
     */
    boolean match(Map<String, Object> item);

    /**
     * Determine a group identifier for given item using current operator.
     *
     * Example:
     * A group-by-attribute operator should return the string version
     * of the required attribute of given item.
     *
     * @param item Item to evaluate
     * @return Group identifier for given item
     */
    String group(Map<String, Object> item);

    /**
     * Calculate value of given items using current operator.
     *
     * Example:
     * An "average" operator can calculate the average value of a given
     * attribute for every item in the given collection.
     *
     * @param items Items to elaborate
     * @return Calculated value
     */
    double calculate(Iterable<Map<String, Object>> items);
}
