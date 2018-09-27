package it.iks.openapm.unit.alerts.evaluators;

import it.iks.openapm.alerts.Condition;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class EvaluatorTestCase {
    /**
     * Shortcut to alert a condition
     *
     * @param isActive True if condition should be active, false otherwise
     * @return Condition object
     */
    protected Condition condition(boolean isActive) {
        Condition condition = mock(Condition.class);
        given(condition.active()).willReturn(isActive);
        return condition;
    }
}
