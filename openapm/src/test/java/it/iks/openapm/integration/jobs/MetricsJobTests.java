package it.iks.openapm.integration.jobs;

import it.iks.openapm.databases.Database;
import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.models.Operation;
import it.iks.openapm.repositories.MetricConfigRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricsJobTests {
    @MockBean
    @Qualifier("metricsDatabase")
    private Database metricsDatabase;

    @MockBean
    @Qualifier("tracesDatabase")
    private Database tracesDatabase;

    @MockBean
    private MetricConfigRepository metricRepository;

    @Autowired
    private JobLauncherTestUtils launcher;

    @Autowired
    private Job metricGenerationJob;

    @Test
    public void it_generates_metrics() throws Exception {
        given(metricRepository.findById("abc123")).willReturn(config());
        given(tracesDatabase.findAllInInterval(anyString(), any(Date.class), any(Date.class))).willReturn(traces());
        InOrder inOrder = inOrder(metricsDatabase);

        launcher.setJob(metricGenerationJob);
        JobExecution execution = launcher.launchJob(parameters());

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        inOrder.verify(metricsDatabase).insert(
                eq("metrics"),
                argThat(metric -> metric.get("name").equals("metric-1")
                        && (double) metric.get("value") == 20.0
                        && metric.get("group").equals("/about")),
                eq("metrics")
        );
        inOrder.verify(metricsDatabase).insert(
                eq("metrics"),
                argThat(metric -> metric.get("name").equals("metric-1")
                        && (double) metric.get("value") == 0.0
                        && metric.get("group").equals("/contact")),
                eq("metrics")
        );
        inOrder.verify(metricsDatabase).insert(
                eq("metrics"),
                argThat(metric -> metric.get("name").equals("metric-1")
                        && (double) metric.get("value") == 25.0
                        && metric.get("group").equals("/")),
                eq("metrics")
        );
    }

    private Optional<MetricConfig> config() {
        MetricConfig config = new MetricConfig();

        config.setId("abc123");
        config.setName("metric-1");
        config.setCron("0 * * * * *");
        config.setDuration(60);
        config.setTracesIndex("traces");
        config.setMetricsIndex("metrics");
        config.setFilters(new Operation[]{
                new Operation(new String[]{"type", "'http'"}, "=")
        });
        config.setAggregation(new Operation(new String[]{"http.url"}));
        config.setCalculation(new Operation(new String[]{"duration_ms"}, "average"));

        return Optional.of(config);
    }

    private JobParameters parameters() {
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("startup_time", new JobParameter(new Date()));
        parameters.put("config_id", new JobParameter("abc123"));

        return new JobParameters(parameters);
    }

    private Iterable<Map<String, Object>> traces() {
        Map<String, Object> trace1 = new HashMap<>();
        trace1.put("type", "http");
        trace1.put("http.url", "/");
        trace1.put("duration_ms", 10);

        Map<String, Object> trace2 = new HashMap<>();
        trace2.put("type", "http");
        trace2.put("http.url", "/about");
        trace2.put("duration_ms", 20);

        Map<String, Object> trace3 = new HashMap<>();
        trace3.put("type", "jdbc");
        trace3.put("host", "localhost");
        trace3.put("duration_ms", 30);

        Map<String, Object> trace4 = new HashMap<>();
        trace4.put("type", "http");
        trace4.put("http.url", "/");
        trace4.put("duration_ms", 40);

        Map<String, Object> trace5 = new HashMap<>();
        trace5.put("type", "smtp");
        trace5.put("smtp.url", "smtp.example.com");
        trace5.put("duration_ms", 5);

        Map<String, Object> trace6 = new HashMap<>();
        trace6.put("type", "http");
        trace6.put("http.url", "/contact");
        trace6.put("duration_ms", "this_is_a_bug");

        return Arrays.asList(trace1, trace2, trace3, trace4, trace5, trace6);
    }
}
