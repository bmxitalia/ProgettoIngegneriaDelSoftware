package it.iks.openapm.unit.templators;

import it.iks.openapm.templators.OperandTemplator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OperandTemplatorTests {
    @Autowired
    private OperandTemplator templator;

    private Map<String, Object> item(String key, Object value) {
        Map<String, Object> item = new HashMap<>();
        item.put(key, value);
        return item;
    }

    @Test
    public void it_fetch_static_values() {
        assertThat(templator.value(null, "'static_value'")).isEqualTo("static_value");
    }

    @Test
    public void it_fetch_properties() {
        assertThat(templator.value(item("attribute", "string"), "attribute")).isEqualTo("string");
        assertThat(templator.value(item("attribute", 123), "attribute")).isEqualTo("123");
        assertThat(templator.value(item("attribute", 123.123), "attribute")).isEqualTo("123.123");
        assertThat(templator.value(item("attribute", 123.123F), "attribute")).isEqualTo("123.123");
        assertThat(templator.value(item("attribute", 123L), "attribute")).isEqualTo("123");
        assertThat(templator.value(item("attribute", new Object()), "attribute")).contains("java.lang.Object@");
        assertThat(templator.value(item("attribute", new Object() {
            @Override
            public String toString() {
                return "Hello World";
            }
        }), "attribute")).isEqualTo("Hello World");
    }
}
