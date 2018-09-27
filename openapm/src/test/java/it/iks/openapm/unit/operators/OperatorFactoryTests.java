package it.iks.openapm.unit.operators;

import it.iks.openapm.models.Operation;
import it.iks.openapm.operators.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OperatorFactoryTests {
    @Autowired
    protected OperatorFactory factory;

    @Test
    public void it_makes_a_null_operator() {
        assertThat(factory.nullOperator()).isInstanceOf(NullOperator.class);
    }

    @Test
    public void it_makes_an_operator_from_a_model() {
        assertThat(factory.byModel(new Operation(new String[]{}, "null"))).isInstanceOf(NullOperator.class);

        assertThat(factory.byModel(new Operation(new String[]{"a", "b"}, "="))).isInstanceOf(EqualsOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{"a", "b"}, ">"))).isInstanceOf(GreaterOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{"a", "b"}, "<"))).isInstanceOf(LessOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{"a"}, "attribute"))).isInstanceOf(AttributeOperator.class);

        assertThat(factory.byModel(new Operation(new String[]{"a"}, "+"))).isInstanceOf(SumOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{"a"}, "average"))).isInstanceOf(AverageOperator.class);
    }

    @Test
    public void it_does_not_make_an_invalid_operator_from_a_model() {
        assertThat(factory.byModel(new Operation(new String[]{}, "="))).isInstanceOf(NullOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{}, ">"))).isInstanceOf(NullOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{}, "<"))).isInstanceOf(NullOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{}, "attribute"))).isInstanceOf(NullOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{}, "average"))).isInstanceOf(NullOperator.class);
        assertThat(factory.byModel(new Operation(new String[]{}, "sum"))).isInstanceOf(NullOperator.class);
    }
}
