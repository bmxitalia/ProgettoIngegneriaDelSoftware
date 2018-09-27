package it.iks.openapm.alerts.evaluators;

import it.iks.openapm.alerts.Condition;

import java.util.List;

/**
 * Evaluator returns true only if all conditions are true
 */
public class AllMatchEvaluator implements Evaluator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean evaluate(List<Condition> conditions) {
        if (conditions.isEmpty()) {
            return false;
        }

        for (Condition condition : conditions) {
            if (!condition.active()) {
                return false;
            }
        }

        return true;
    }
}
