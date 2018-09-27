package it.iks.openapm.unit.alerts;

import it.iks.openapm.alerts.MetricState;
import it.iks.openapm.operators.AttributeOperator;
import it.iks.openapm.operators.EqualsOperator;
import it.iks.openapm.operators.Operator;
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
@SpringBootTest()
public class MetricStateTests {
    @Autowired
    private OperandTemplator templator;

    @Test
    public void it_set_metric_with_one_filter() {
        MetricState state = new MetricState(new Operator[]{
                new EqualsOperator(new String[]{"value", "'abc'"}, templator)
        });
        Map<String, Object> metric = new HashMap<>();
        metric.put("value", "abc");

        assertThat(state.getLastMetric()).isNull();
        state.evaluateMetric(metric);
        assertThat(state.getLastMetric()).isSameAs(metric);
    }

    @Test
    public void it_does_not_set_metric_with_one_filter() {
        MetricState state = new MetricState(new Operator[]{
                new EqualsOperator(new String[]{"value", "'abc'"}, templator)
        });
        Map<String, Object> metric = new HashMap<>();
        metric.put("value", "def");

        assertThat(state.getLastMetric()).isNull();
        state.evaluateMetric(metric);
        assertThat(state.getLastMetric()).isNull();
    }

    @Test
    public void it_set_metric_with_multiple_filters() {
        MetricState state = new MetricState(new Operator[]{
                new EqualsOperator(new String[]{"value", "'abc'"}, templator),
                new AttributeOperator(new String[]{"something"}, templator)
        });
        Map<String, Object> metric = new HashMap<>();
        metric.put("value", "abc");
        metric.put("something", "123");

        assertThat(state.getLastMetric()).isNull();
        state.evaluateMetric(metric);
        assertThat(state.getLastMetric()).isSameAs(metric);
    }

    @Test
    public void it_does_not_set_metric_when_all_filters_fails() {
        MetricState state = new MetricState(new Operator[]{
                new EqualsOperator(new String[]{"value", "'abc'"}, templator),
                new AttributeOperator(new String[]{"something"}, templator)
        });
        Map<String, Object> metric = new HashMap<>();
        metric.put("value", "def");

        assertThat(state.getLastMetric()).isNull();
        state.evaluateMetric(metric);
        assertThat(state.getLastMetric()).isNull();
    }

    @Test
    public void it_does_not_set_metric_when_some_filters_fails() {
        MetricState state = new MetricState(new Operator[]{
                new EqualsOperator(new String[]{"value", "'abc'"}, templator),
                new AttributeOperator(new String[]{"something"}, templator)
        });
        Map<String, Object> metric = new HashMap<>();
        metric.put("value", "def");
        metric.put("something", "123");

        assertThat(state.getLastMetric()).isNull();
        state.evaluateMetric(metric);
        assertThat(state.getLastMetric()).isNull();
    }
}
