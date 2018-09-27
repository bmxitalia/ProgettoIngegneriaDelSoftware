package it.iks.openapm.operators;

import it.iks.openapm.templators.OperandTemplator;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.StreamSupport;

/**
 * Operator to calculate average of an attribute in a set of traces.
 */
public class AverageOperator extends AbstractOperator {
    public AverageOperator(String[] operands, OperandTemplator templator) {
        super(operands, templator);
    }

    @Override
    public boolean valid() {
        return operands.length == 1;
    }

    @Override
    public double calculate(Iterable<Map<String, Object>> items) {
        OptionalDouble optional = StreamSupport.stream(items.spliterator(), false)
                .filter(item -> hasAttribute(operands[0], item))
                .mapToDouble(item -> attributeValue(operands[0], item))
                .average();

        if (optional.isPresent()) {
            return optional.getAsDouble();
        }

        return 0;
    }
}
