package it.iks.openapm.unit.dispatchers;

import it.iks.openapm.dispatchers.BaselineGenerationDispatcher;
import it.iks.openapm.dispatchers.DispatchersFactory;
import it.iks.openapm.dispatchers.MetricGenerationDispatcher;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.models.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.config.CronTask;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DispatchersFactoryTests {
    @Autowired
    private DispatchersFactory factory;

    @Test
    public void it_builds_metrics_dispatcher() {
        MetricConfig config = new MetricConfig("metric-1", "0 * * * * *", "traces", "metrics");
        config.setId("abc123");

        CronTask task = factory.metric(config);

        assertThat(task.getExpression()).isEqualTo("0 * * * * *");
        assertThat(task.getRunnable()).isInstanceOf(MetricGenerationDispatcher.class);
    }

    @Test
    public void it_builds_baselines_dispatcher() {
        BaselineConfig config = new BaselineConfig(
                "abc123",
                "baseline-1",
                "metrics",
                "baselines",
                "daily",
                new Operation[]{},
                null,
                null
        );

        CronTask task = factory.baseline(config);

        assertThat(task.getExpression()).isEqualTo("0 0 * * * *");
        assertThat(task.getRunnable()).isInstanceOf(BaselineGenerationDispatcher.class);
    }
}
