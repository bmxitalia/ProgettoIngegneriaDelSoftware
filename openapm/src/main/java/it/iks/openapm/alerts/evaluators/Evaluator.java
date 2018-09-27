package it.iks.openapm.alerts.evaluators;

import it.iks.openapm.alerts.Condition;

import java.util.List;

/**
 * An implementation of the logic that from a set of conditions' state defines a global output state
 *
 * Examples of evaluators can be "match all conditions" or "match any conditions".
 */
@FunctionalInterface
public interface Evaluator {
    /**
     * Evaluate a list of conditions to determine global state
     *
     * @param conditions List of evaluated conditions
     * @return True on global match, false otherwise
     */
    boolean evaluate(List<Condition> conditions);
}
