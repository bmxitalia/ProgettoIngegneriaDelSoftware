package it.iks.openapm.unit.alerts.evaluators;

import it.iks.openapm.alerts.evaluators.AllMatchEvaluator;
import it.iks.openapm.alerts.evaluators.Evaluator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AllMatchEvaluatorTests extends EvaluatorTestCase {
    @Test
    public void it_matches_on_one_element_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(Collections.singletonList(condition(true)))).isTrue();
    }

    @Test
    public void it_does_not_match_on_one_element_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(Collections.singletonList(condition(false)))).isFalse();
    }

    @Test
    public void it_matches_on_multiple_elements_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(Arrays.asList(
                condition(true),
                condition(true)
        ))).isTrue();
    }

    @Test
    public void it_does_not_match_on_discordant_multiple_elements_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(Arrays.asList(
                condition(true),
                condition(false)
        ))).isFalse();
    }

    @Test
    public void it_does_not_match_on_multiple_elements_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(Arrays.asList(
                condition(false),
                condition(false)
        ))).isFalse();
    }

    @Test
    public void it_does_not_match_on_empty_list() {
        Evaluator evaluator = new AllMatchEvaluator();
        assertThat(evaluator.evaluate(new ArrayList<>())).isFalse();
    }
}
