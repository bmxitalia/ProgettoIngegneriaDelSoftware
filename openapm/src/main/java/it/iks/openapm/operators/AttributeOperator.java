package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;

/**
 * Operator to retrieve an attribute from item.
 *
 * The item is match if the attribute exists and is not null.
 * The group will be the value of the item.
 */
public class AttributeOperator extends AbstractOperator {
    public AttributeOperator(String[] operands, OperandTemplator templator) {
        super(operands, templator);
    }

    @Override
    public boolean valid() {
        return operands.length == 1;
    }

    @Override
    public boolean match(Map<String, Object> item) {
        return templator.value(item, operands[0]) != null;
    }

    @Override
    public String group(Map<String, Object> item) {
        return templator.value(item, operands[0]);
    }

    @Override
    public double calculate(Iterable<Map<String, Object>> items) {
        for (Map<String, Object> item : items) {
            if (item != null && hasAttribute(operands[0], item)) {
                return attributeValue(operands[0], item);
            }
        }

        return 0;
    }
}
