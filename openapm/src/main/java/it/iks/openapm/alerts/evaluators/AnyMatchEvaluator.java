package it.iks.openapm.alerts.evaluators;

import it.iks.openapm.alerts.Condition;

import java.util.List;

/**
 * Evaluator returns true if any of given conditions is true
 */
public class AnyMatchEvaluator implements Evaluator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(List<Condition> conditions) {
        for (Condition condition : conditions) {
            if (condition.active()) {
                return true;
            }
        }

        return false;
    }
}
