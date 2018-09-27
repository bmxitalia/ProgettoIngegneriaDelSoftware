package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;

/**
 * Operator to match if first operand's values is greater than second operand's value
 */
public class GreaterOperator extends AbstractOperator {

    public GreaterOperator(String[] operands, OperandTemplator templator) {
        super(operands, templator);
    }

    @Override
    public boolean valid() {
        return operands.length == 2;
    }

    @Override
    public boolean match(Map<String, Object> item) {
        String a = templator.value(item, operands[0]);
        String b = templator.value(item, operands[1]);

        if (a == null || b == null) {
            return false;
        }

        try {
            return Double.parseDouble(a) > Double.parseDouble(b);
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
