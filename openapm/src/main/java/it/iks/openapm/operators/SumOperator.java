package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;

/**
 * Operator to calculate sum of attributes in a set of items.
 */
public class SumOperator extends AbstractOperator {
    public SumOperator(String[] operands, OperandTemplator templator) {
        super(operands, templator);
    }

    @Override
    public boolean valid() {
        return operands.length > 0;
    }

    @Override
    public double calculate(Iterable<Map<String, Object>> items) {
        double sum = 0;

        for (Map<String, Object> item : items) {
            sum += itemSum(item);
        }

        return sum;
    }

    private double itemSum(Map<String, Object> item) {
        double sum = 0;

        for (String operand : operands) {
            if (item != null && hasAttribute(operand, item)) {
                sum += attributeValue(operand, item);
            }
        }

        return sum;
    }
}
