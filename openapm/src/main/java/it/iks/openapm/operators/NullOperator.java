package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;

/**
 * Null operator that doesn't do any calculation or operation
 *
 * Accept everything, don't group, don't calculate
 */
public class NullOperator extends AbstractOperator {

    public NullOperator(String[] operands, OperandTemplator templator) {
        super(operands, templator);
    }

    @Override
    public boolean match(Map<String, Object> item) {
        return true;
    }

    @Override
    public String group(Map<String, Object> item) {
        return "all";
    }

    @Override
    public double calculate(Iterable<Map<String, Object>> items) {
        return 0;
    }
}
