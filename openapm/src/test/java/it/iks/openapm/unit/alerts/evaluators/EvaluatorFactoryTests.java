package it.iks.openapm.unit.alerts.evaluators;

import it.iks.openapm.alerts.evaluators.AllMatchEvaluator;
import it.iks.openapm.alerts.evaluators.AnyMatchEvaluator;
import it.iks.openapm.alerts.factories.EvaluatorFactory;
import it.iks.openapm.alerts.exceptions.MissingEvaluatorException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EvaluatorFactoryTests {
    @Autowired
    protected EvaluatorFactory factory;

    @Test
    public void it_makes_an_evaluator_by_identifier() {
        assertThat(factory.byIdentifier("all_match")).isInstanceOf(AllMatchEvaluator.class);
        assertThat(factory.byIdentifier("any_match")).isInstanceOf(AnyMatchEvaluator.class);
    }

    @Test
    public void it_throws_exception_if_evaluator_does_not_exists() {
        assertThatExceptionOfType(MissingEvaluatorException.class)
                .isThrownBy(() -> factory.byIdentifier("not_found"));
    }
}
