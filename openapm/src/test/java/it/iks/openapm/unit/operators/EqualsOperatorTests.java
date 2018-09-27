package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.EqualsOperator;
import it.iks.openapm.operators.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EqualsOperatorTests extends OperatorTestCase {

    /**
     * Build an item with two attributes (first, second) with given values
     *
     * @param first  Value of "first" attribute
     * @param second Value of "second" attribute
     * @return Populated item
     */
    private Map<String, Object> doubleItem(Object first, Object second) {
        Map<String, Object> item = item("first", first);
        item.put("second", second);
        return item;
    }

    @Test
    public void it_must_have_two_operands() {
        Operator noOperands = new EqualsOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new EqualsOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isFalse();

        Operator twoOperands = new EqualsOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isTrue();

        Operator threeOperands = new EqualsOperator(new String[]{"a", "b", "c"}, templator);
        assertThat(threeOperands.valid()).isFalse();
    }

    @Test
    public void it_match_to_static_correctly() {
        Operator operator = new EqualsOperator(new String[]{"attribute", "'123'"}, templator);

        assertThat(operator.match(item("attribute", "123"))).isTrue();
        assertThat(operator.match(item("attribute", 123))).isTrue();
        assertThat(operator.match(item("attribute", 123L))).isTrue();

        // Double/Float string representation is: 123.0
        assertThat(operator.match(item("attribute", (double) 123))).isFalse();
        assertThat(operator.match(item("attribute", 123F))).isFalse();
    }

    @Test
    public void it_match_to_another_property_correctly() {
        Operator operator = new EqualsOperator(new String[]{"first", "second"}, templator);

        assertThat(operator.match(doubleItem("abc", "abc"))).isTrue();
        assertThat(operator.match(doubleItem((double) 123, (double) 123))).isTrue();
        assertThat(operator.match(doubleItem((double) 123, 123F))).isTrue();
        assertThat(operator.match(doubleItem(123, 123L))).isTrue();
        assertThat(operator.match(doubleItem(123, "123"))).isTrue();

        assertThat(operator.match(doubleItem("abc", "def"))).isFalse();
        assertThat(operator.match(doubleItem(123, "abc"))).isFalse();
    }

    @Test
    public void it_does_not_match_null_correctly() {
        Operator operator = new EqualsOperator(new String[]{"attribute", "'123'"}, templator);

        assertThat(operator.match(item("something", "else"))).isFalse();
        assertThat(operator.match(item("attribute", null))).isFalse();
        assertThat(operator.match(item("attribute", new Object()))).isFalse();
    }

    @Test
    public void it_groups_correctly() {
        Operator operator = new EqualsOperator(new String[]{"attribute", "'123'"}, templator);

        assertThat(operator.group(item("attribute", "123"))).isEqualTo("true");
        assertThat(operator.group(item("attribute", 123))).isEqualTo("true");
        assertThat(operator.group(item("attribute", 123L))).isEqualTo("true");
        assertThat(operator.group(item("attribute", (double) 123))).isEqualTo("false");
        assertThat(operator.group(item("attribute", 123F))).isEqualTo("false");
        assertThat(operator.group(item("something", "else"))).isEqualTo("false");
        assertThat(operator.group(item("attribute", null))).isEqualTo("false");
        assertThat(operator.group(item("attribute", new Object()))).isEqualTo("false");
    }

    @Test
    public void it_cannot_calculate() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new EqualsOperator(new String[]{"attribute", "'abc'"}, templator);

                    operator.calculate(Arrays.asList(
                            item("attribute", "abc"),
                            item("attribute", "def")
                    ));
                });
    }
}
