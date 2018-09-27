package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.AttributeOperator;
import it.iks.openapm.operators.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AttributeOperatorTests extends OperatorTestCase {

    @Test
    public void it_must_have_one_operand() {
        Operator noOperands = new AttributeOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new AttributeOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isTrue();

        Operator twoOperands = new AttributeOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isFalse();
    }

    @Test
    public void it_match_correctly() {
        Operator operator = new AttributeOperator(new String[]{"attribute"}, templator);

        assertThat(operator.match(item("attribute", "string"))).isTrue();
        assertThat(operator.match(item("attribute", 123))).isTrue();
        assertThat(operator.match(item("attribute", 123.123))).isTrue();
        assertThat(operator.match(item("attribute", 123.123F))).isTrue();
        assertThat(operator.match(item("attribute", 123L))).isTrue();
        assertThat(operator.match(item("attribute", new Object()))).isTrue();
    }

    @Test
    public void it_does_not_match_correctly() {
        Operator operator = new AttributeOperator(new String[]{"attribute"}, templator);

        assertThat(operator.match(item("something", "else"))).isFalse();
        assertThat(operator.match(item("attribute", null))).isFalse();
    }

    @Test
    public void it_groups_correctly() {
        Operator operator = new AttributeOperator(new String[]{"attribute"}, templator);

        assertThat(operator.group(item("attribute", "groupA"))).isEqualTo("groupA");
        assertThat(operator.group(item("attribute", 123))).isEqualTo("123");
        assertThat(operator.group(item("attribute", 123.123))).isEqualTo("123.123");
        assertThat(operator.group(item("attribute", 123.123F))).isEqualTo("123.123");
        assertThat(operator.group(item("attribute", 123L))).isEqualTo("123");
        assertThat(operator.group(item("something", "else"))).isNullOrEmpty();
    }

    @Test
    public void it_fetches_first_item_with_double_attribute() {
        Operator operator = new AttributeOperator(new String[]{"attribute"}, templator);

        assertThat(operator.calculate(Collections.singletonList(item("attribute", "not_double")))).isEqualTo(0);
        assertThat(operator.calculate(Collections.singletonList(item("attribute", 12)))).isEqualTo(12);
        assertThat(operator.calculate(Arrays.asList(item("attribute", "not_double"), item("attribute", 12)))).isEqualTo(12);
    }
}
