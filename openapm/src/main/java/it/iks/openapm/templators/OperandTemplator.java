package it.iks.openapm.templators;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Templator to manage operands in operations.
 */
@Component
public class OperandTemplator {
    /**
     * Grab the value identified by operand identifier of given object
     *
     * @param item       Item being worked on
     * @param identifier Operand identifier to be parsed
     * @return Operand value
     */
    public String value(Map<String, Object> item, String identifier) {
        if (isStatic(identifier)) {
            return processStatic(identifier);
        }

        return fetch(item, identifier);
    }

    /**
     * Process identifier as static variable.
     *
     * Remember to check if it's static using {@link #isStatic(String)} first.
     * It just strips first and last characters (expected to be apostrophes).
     *
     * @param identifier Operand identifier to be parsed
     * @return Operand value
     */
    private String processStatic(String identifier) {
        return identifier.substring(1, identifier.length() - 1);
    }

    /**
     * Determine if current identifier is a static string
     *
     * At the current status it just check if string starts and ends with
     * an apostrophe. We might consider switch to a json-like representation.
     *
     * @param identifier Operand identifier to be parsed
     * @return True if operand is static
     */
    private boolean isStatic(String identifier) {
        return identifier.startsWith("'") && identifier.endsWith("'");
    }

    /**
     * Fetch item value using identifier
     *
     * @param item       Item being worked on
     * @param identifier Operand identifier to be parsed
     * @return Value as a string
     */
    private String fetch(Map<String, Object> item, String identifier) {
        if (!item.containsKey(identifier)) {
            return null;
        }

        Object value = item.get(identifier);

        if (value == null) {
            return null;
        }

        return value.toString();
    }
}
