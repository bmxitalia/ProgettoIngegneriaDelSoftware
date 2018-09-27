package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.NullOperator;
import it.iks.openapm.operators.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NullOperatorTests extends OperatorTestCase {

    @Test
    public void it_can_have_any_operands_count() {
        Operator noOperands = new NullOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isTrue();

        Operator oneOperand = new NullOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isTrue();

        Operator twoOperands = new NullOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isTrue();
    }

    @Test
    public void it_match_correctly() {
        Operator operator = new NullOperator(new String[]{}, templator);

        assertThat(operator.match(item("attribute", "string"))).isTrue();
        assertThat(operator.match(item("attribute", 123))).isTrue();
        assertThat(operator.match(item("attribute", 123.123))).isTrue();
        assertThat(operator.match(item("attribute", 123.123F))).isTrue();
        assertThat(operator.match(item("attribute", 123L))).isTrue();
        assertThat(operator.match(item("attribute", new Object()))).isTrue();
        assertThat(operator.match(item("something", "else"))).isTrue();
        assertThat(operator.match(item("attribute", null))).isTrue();
    }

    @Test
    public void it_groups_correctly() {
        Operator operator = new NullOperator(new String[]{}, templator);

        assertThat(operator.group(item("attribute", "groupA"))).isEqualTo("all");
        assertThat(operator.group(item("attribute", 123))).isEqualTo("all");
        assertThat(operator.group(item("attribute", 123.123))).isEqualTo("all");
        assertThat(operator.group(item("attribute", 123.123F))).isEqualTo("all");
        assertThat(operator.group(item("attribute", 123L))).isEqualTo("all");
        assertThat(operator.group(item("attribute", new Object()))).isEqualTo("all");
        assertThat(operator.group(item("something", "else"))).isEqualTo("all");
        assertThat(operator.group(item("attribute", null))).isEqualTo("all");
    }

    @Test
    public void it_calculate_correctly() {
        Operator operator = new NullOperator(new String[]{"attribute"}, templator);

        assertThat(operator.calculate(Arrays.asList(
                item("attribute", "abc"),
                item("attribute", 123),
                item("attribute", 123F),
                item("attribute", new Object()),
                item("attribute", null)
        ))).isEqualTo(0);
    }
}
