package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.LessOperator;
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
public class LessOperatorTests extends OperatorTestCase {

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
        Operator noOperands = new LessOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new LessOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isFalse();

        Operator twoOperands = new LessOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isTrue();

        Operator threeOperands = new LessOperator(new String[]{"a", "b", "c"}, templator);
        assertThat(threeOperands.valid()).isFalse();
    }

    @Test
    public void it_match_to_static_correctly() {
        Operator operator = new LessOperator(new String[]{"attribute", "'124'"}, templator);

        assertThat(operator.match(item("attribute", "123"))).isTrue();
        assertThat(operator.match(item("attribute", "123.45"))).isTrue();
        assertThat(operator.match(item("attribute", 123))).isTrue();
        assertThat(operator.match(item("attribute", 123L))).isTrue();
        assertThat(operator.match(item("attribute", 123.45))).isTrue();
        assertThat(operator.match(item("attribute", 123F))).isTrue();
    }

    @Test
    public void it_does_not_match_to_static_correctly() {
        Operator operator = new LessOperator(new String[]{"attribute", "'100'"}, templator);

        assertThat(operator.match(item("attribute", "123"))).isFalse();
        assertThat(operator.match(item("attribute", "123.45"))).isFalse();
        assertThat(operator.match(item("attribute", 123))).isFalse();
        assertThat(operator.match(item("attribute", 123L))).isFalse();
        assertThat(operator.match(item("attribute", 123.45))).isFalse();
        assertThat(operator.match(item("attribute", 123F))).isFalse();
    }

    @Test
    public void it_match_to_another_property_correctly() {
        Operator operator = new LessOperator(new String[]{"first", "second"}, templator);

        assertThat(operator.match(doubleItem(123, 123.45D))).isTrue();
        assertThat(operator.match(doubleItem(123.123F, 123.45D))).isTrue();
        assertThat(operator.match(doubleItem(123L, 125))).isTrue();
        assertThat(operator.match(doubleItem("100", 123))).isTrue();
    }

    @Test
    public void it_does_not_match_to_another_property_correctly() {
        Operator operator = new LessOperator(new String[]{"first", "second"}, templator);

        assertThat(operator.match(doubleItem("abc", "abc"))).isFalse();
        assertThat(operator.match(doubleItem(123.45D, 123))).isFalse();
        assertThat(operator.match(doubleItem(123.45F, 123D))).isFalse();
        assertThat(operator.match(doubleItem(125, 123L))).isFalse();
        assertThat(operator.match(doubleItem(123, "100"))).isFalse();
        assertThat(operator.match(doubleItem("def", "abc"))).isFalse();
        assertThat(operator.match(doubleItem("abc", 123))).isFalse();
    }

    @Test
    public void it_does_not_match_null_correctly() {
        Operator operator = new LessOperator(new String[]{"attribute", "'123'"}, templator);

        assertThat(operator.match(item("something", "else"))).isFalse();
        assertThat(operator.match(item("attribute", null))).isFalse();
        assertThat(operator.match(item("attribute", new Object()))).isFalse();
    }

    @Test
    public void it_cannot_calculate() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new LessOperator(new String[]{"attribute", "'abc'"}, templator);

                    operator.calculate(Arrays.asList(
                            item("attribute", "abc"),
                            item("attribute", "def")
                    ));
                });
    }
}
