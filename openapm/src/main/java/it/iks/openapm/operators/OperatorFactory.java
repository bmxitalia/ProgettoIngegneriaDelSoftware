package it.iks.openapm.operators;

import it.iks.openapm.models.Operation;
import it.iks.openapm.templators.OperandTemplator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to alert Operator objects.
 */
@Component
public class OperatorFactory {
    private static final Logger log = LoggerFactory.getLogger(OperatorFactory.class);

    /**
     * Table mapping operator field to concrete operator class
     */
    private static final Map<String, Class<? extends Operator>> operatorsTable;

    static {
        Map<String, Class<? extends Operator>> tempOperatorsTable = new HashMap<>();

        tempOperatorsTable.put("null", NullOperator.class);

        // Match/Group operators
        tempOperatorsTable.put("=", EqualsOperator.class);
        tempOperatorsTable.put(">", GreaterOperator.class);
        tempOperatorsTable.put("<", LessOperator.class);
        tempOperatorsTable.put("attribute", AttributeOperator.class);

        // Calculation operators
        tempOperatorsTable.put("+", SumOperator.class);
        tempOperatorsTable.put("average", AverageOperator.class);

        operatorsTable = Collections.unmodifiableMap(tempOperatorsTable);
    }

    /**
     * Templator to process operands
     */
    private final OperandTemplator templator;

    /**
     * Initialize OperatorFactory.
     *
     * @param templator Templator to process operands
     */
    @Autowired
    public OperatorFactory(OperandTemplator templator) {
        this.templator = templator;
    }

    /**
     * Build an operator for given operation model
     *
     * @param operation Operation model
     * @return Operator model
     */
    public Operator byModel(Operation operation) {
        Operator operator = buildOperator(operation.getOperator(), operation.getOperands());

        if (!operator.valid()) {
            log.warn(String.format(
                    "Operator \"%s\" doesn't support \"%s\" operands.",
                    operator,
                    Arrays.toString(operation.getOperands())
            ));
            return new NullOperator(operation.getOperands(), templator);
        }

        return operator;
    }

    private Operator buildOperator(String operator, String[] operands) {
        if (operator == null) {
            return new AttributeOperator(operands, templator);
        }

        if (!operatorsTable.containsKey(operator)) {
            log.warn(String.format("Operator \"%s\" not found", operator));
            return new NullOperator(operands, templator);
        }

        try {
            Constructor<? extends Operator> constructor = operatorsTable.get(operator)
                    .getConstructor(String[].class, OperandTemplator.class);

            return constructor.newInstance(operands, templator);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            // This should never happen since we are using a reference table
            log.warn(String.format("Cannot create operator \"%s\"", operator), e);
            return new NullOperator(operands, templator);
        }
    }

    /**
     * Build a null operator
     *
     * @return NullOperator instance
     */
    public Operator nullOperator() {
        return new NullOperator(new String[]{}, templator);
    }

    /**
     * Build an average operator
     *
     * @param attribute Attribute key to use for values
     * @return AverageOperator instance
     */
    public Operator average(String attribute) {
        return new AverageOperator(new String[]{attribute}, templator);
    }
}
