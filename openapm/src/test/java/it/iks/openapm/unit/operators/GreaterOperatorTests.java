package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.GreaterOperator;
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
public class GreaterOperatorTests extends OperatorTestCase {

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
        Operator noOperands = new GreaterOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new GreaterOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isFalse();

        Operator twoOperands = new GreaterOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isTrue();

        Operator threeOperands = new GreaterOperator(new String[]{"a", "b", "c"}, templator);
        assertThat(threeOperands.valid()).isFalse();
    }

    @Test
    public void it_match_to_static_correctly() {
        Operator operator = new GreaterOperator(new String[]{"attribute", "'122'"}, templator);

        assertThat(operator.match(item("attribute", "123"))).isTrue();
        assertThat(operator.match(item("attribute", "123.45"))).isTrue();
        assertThat(operator.match(item("attribute", 123))).isTrue();
        assertThat(operator.match(item("attribute", 123L))).isTrue();
        assertThat(operator.match(item("attribute", 123.45))).isTrue();
        assertThat(operator.match(item("attribute", 123F))).isTrue();
    }

    @Test
    public void it_does_not_match_to_static_correctly() {
        Operator operator = new GreaterOperator(new String[]{"attribute", "'456'"}, templator);

        assertThat(operator.match(item("attribute", "123"))).isFalse();
        assertThat(operator.match(item("attribute", "123.45"))).isFalse();
        assertThat(operator.match(item("attribute", 123))).isFalse();
        assertThat(operator.match(item("attribute", 123L))).isFalse();
        assertThat(operator.match(item("attribute", 123.45))).isFalse();
        assertThat(operator.match(item("attribute", 123F))).isFalse();
    }

    @Test
    public void it_match_to_another_property_correctly() {
        Operator operator = new GreaterOperator(new String[]{"first", "second"}, templator);

        assertThat(operator.match(doubleItem(123.45D, 123))).isTrue();
        assertThat(operator.match(doubleItem(123.45D, 123.123F))).isTrue();
        assertThat(operator.match(doubleItem(125, 123L))).isTrue();
        assertThat(operator.match(doubleItem(123, "100"))).isTrue();
    }

    @Test
    public void it_does_not_match_to_another_property_correctly() {
        Operator operator = new GreaterOperator(new String[]{"first", "second"}, templator);

        assertThat(operator.match(doubleItem("abc", "abc"))).isFalse();
        assertThat(operator.match(doubleItem(123, 123.45D))).isFalse();
        assertThat(operator.match(doubleItem(123D, 123.45F))).isFalse();
        assertThat(operator.match(doubleItem(123L, 125))).isFalse();
        assertThat(operator.match(doubleItem("100", 123))).isFalse();
        assertThat(operator.match(doubleItem("def", "abc"))).isFalse();
        assertThat(operator.match(doubleItem("abc", 123))).isFalse();
    }

    @Test
    public void it_does_not_match_null_correctly() {
        Operator operator = new GreaterOperator(new String[]{"attribute", "'123'"}, templator);

        assertThat(operator.match(item("something", "else"))).isFalse();
        assertThat(operator.match(item("attribute", null))).isFalse();
        assertThat(operator.match(item("attribute", new Object()))).isFalse();
    }

    @Test
    public void it_cannot_calculate() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new GreaterOperator(new String[]{"attribute", "'abc'"}, templator);

                    operator.calculate(Arrays.asList(
                            item("attribute", "abc"),
                            item("attribute", "def")
                    ));
                });
    }
}
