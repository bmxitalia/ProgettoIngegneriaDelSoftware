package it.iks.openapm.unit.operators;

import it.iks.openapm.templators.OperandTemplator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class OperatorTestCase {
    @Autowired
    protected OperandTemplator templator;

    /**
     * Make a one-attribute item shortcut
     *
     * @param key
     * @param value
     * @return
     */
    Map<String, Object> item(String key, Object value) {
        Map<String, Object> item = new HashMap<>();
        item.put(key, value);
        return item;
    }
}
