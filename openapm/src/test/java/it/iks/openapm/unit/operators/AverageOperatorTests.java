package it.iks.openapm.unit.operators;

import it.iks.openapm.operators.AverageOperator;
import it.iks.openapm.operators.Operator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AverageOperatorTests extends OperatorTestCase {

    @Test
    public void it_must_have_one_operand() {
        Operator noOperands = new AverageOperator(new String[]{}, templator);
        assertThat(noOperands.valid()).isFalse();

        Operator oneOperand = new AverageOperator(new String[]{"a"}, templator);
        assertThat(oneOperand.valid()).isTrue();

        Operator twoOperands = new AverageOperator(new String[]{"a", "b"}, templator);
        assertThat(twoOperands.valid()).isFalse();
    }

    @Test
    public void it_calculates_correctly() {
        Operator operator = new AverageOperator(new String[]{"attribute"}, templator);

        assertThat(operator.calculate(Arrays.asList(
                item("attribute", "ignored"),
                item("attribute", 1),
                item("attribute", 1.2),
                item("attribute", 1.2F),
                item("attribute", 1L),
                item("something", "else")
        ))).isEqualTo(1.100000011920929); // Floating-point arithmetic ❤️
    }

    @Test
    public void it_cannot_match() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new AverageOperator(new String[]{"attribute"}, templator);

                    operator.match(item("attribute", "abc"));
                });
    }

    @Test
    public void it_cannot_group() {
        assertThatExceptionOfType(UnsupportedOperationException.class)
                .isThrownBy(() -> {
                    Operator operator = new AverageOperator(new String[]{"attribute"}, templator);

                    operator.group(item("attribute", "abc"));
                });
    }
}
