package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;

/**
 * Base operator implementation
 */
abstract public class AbstractOperator implements Operator {
    /**
     * Operands elements
     */
    protected final String[] operands;

    /**
     * Templator to parse operand value
     */
    protected final OperandTemplator templator;

    /**
     * Initialize AbstractOperator.
     *
     * @param operands  Operands elements
     * @param templator Templator to parse operand value
     */
    public AbstractOperator(String[] operands, OperandTemplator templator) {
        this.operands = operands;
        this.templator = templator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean valid() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean match(Map<String, Object> item) {
        throw new UnsupportedOperationException("Match operator not supported");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String group(Map<String, Object> item) {
        return match(item) ? "true" : "false";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double calculate(Iterable<Map<String, Object>> items) {
        throw new UnsupportedOperationException("Calculate operator not supported");
    }

    /**
     * Determine if the needed attribute is available and is a double
     *
     * @param attribute Attribute key
     * @param item      Item
     * @return True if attribute is a double
     */
    protected boolean hasAttribute(String attribute, Map<String, Object> item) {
        if (!item.containsKey(attribute)) {
            return false;
        }

        Object value = item.get(attribute);

        if (value instanceof Double || value instanceof Integer || value instanceof Long || value instanceof Float) {
            return true;
        }

        try {
            Double.parseDouble((String) value);
        } catch (ClassCastException | NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Retrieve attribute value from item and cast it to double
     *
     * @param attribute Attribute key
     * @param item      Item
     * @return Attribute value casted to double
     */
    protected double attributeValue(String attribute, Map<String, Object> item) {
        Object value = item.get(attribute);

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return Double.parseDouble(value.toString());
    }
}
