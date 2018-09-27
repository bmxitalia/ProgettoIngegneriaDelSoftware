package it.iks.openapm.integration.jobs;

import it.iks.openapm.databases.Database;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.Operation;
import it.iks.openapm.repositories.BaselineConfigRepository;
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

import static it.iks.openapm.TestUtils.parseJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaselineJobTests {
    @MockBean
    @Qualifier("baselinesDatabase")
    private Database baselinesDatabase;

    @MockBean
    @Qualifier("metricsDatabase")
    private Database metricsDatabase;

    @MockBean
    private BaselineConfigRepository baselineRepository;

    @Autowired
    private JobLauncherTestUtils launcher;

    @Autowired
    private Job baselineGenerationJob;

    @Test
    public void it_generates_daily_baselines() throws Exception {
        given(baselineRepository.findById(eq("abc123"))).willReturn(dailyConfig());
        given(metricsDatabase.findAllInInterval(
                eq("metrics"), eq(new Date(1515769200000L)), eq(new Date(1515772800000L)))
        ).willReturn(firstMetrics());
        given(metricsDatabase.findAllInInterval(
                eq("metrics"), eq(new Date(1515855600000L)), eq(new Date(1515859200000L)))
        ).willReturn(secondMetrics());
        given(metricsDatabase.findAllInInterval(
                eq("metrics"), eq(new Date(1515942000000L)), eq(new Date(1515945600000L)))
        ).willReturn(thirdMetrics());
        given(metricsDatabase.findAllInInterval(
                eq("metrics"), eq(new Date(1516028400000L)), eq(new Date(1516032000000L)))
        ).willReturn(fourthMetrics());
        InOrder inOrder = inOrder(baselinesDatabase);

        launcher.setJob(baselineGenerationJob);
        JobExecution execution = launcher.launchJob(parameters());

        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        inOrder.verify(baselinesDatabase).insert(
                eq("baselines"),
                argThat(baseline -> baseline.get("name").equals("baseline-1")
                        && baseline.get("group").equals("/about")
                        && (double) baseline.get("mean") == 10.557125
                        && (double) baseline.get("deviation") == 0.9084302499901807
                        && (int) baseline.get("hour") == 16
                        && baseline.get("strategy").equals("daily")),
                eq("baselines")
        );
        inOrder.verify(baselinesDatabase).insert(
                eq("baselines"),
                argThat(baseline -> baseline.get("name").equals("baseline-1")
                        && baseline.get("group").equals("/contact")
                        && (double) baseline.get("mean") == 10.294666666666666
                        && (double) baseline.get("deviation") == 0.2622937284801138
                        && (int) baseline.get("hour") == 16
                        && baseline.get("strategy").equals("daily")),
                eq("baselines")
        );
        inOrder.verify(baselinesDatabase).insert(
                eq("baselines"),
                argThat(baseline -> baseline.get("name").equals("baseline-1")
                        && baseline.get("group").equals("/")
                        && (double) baseline.get("mean") == 10.904395833333332
                        && (double) baseline.get("deviation") == 1.0396652562493773
                        && (int) baseline.get("hour") == 16
                        && baseline.get("strategy").equals("daily")),
                eq("baselines")
        );
    }

    private Optional<BaselineConfig> dailyConfig() {
        BaselineConfig config = new BaselineConfig();

        config.setId("abc123");
        config.setName("baseline-1");
        config.setBaselinesIndex("baselines");
        config.setMetricsIndex("metrics");
        config.setStrategy("daily");
        config.setFilters(new Operation[]{
                new Operation(new String[]{"name", "'http-duration-by-page'"}, "=")
        });
        config.setAggregation(new Operation(new String[]{"group"}));
        config.setCalculationField("value");

        return Optional.of(config);
    }

    private JobParameters parameters() {
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("calculation_time", new JobParameter(new Date(1516028400000L))); // 2018-01-15 15.00
        parameters.put("config_id", new JobParameter("abc123"));

        return new JobParameters(parameters);
    }

    private Iterable<Map<String, Object>> firstMetrics() {
        return Arrays.asList(
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.307}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.818}"),
                parseJson("{\"name\":\"jdbc-duration-by-host\",\"group\":\"localhost\",\"value\":12.2}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.181}"),
                parseJson("{\"name\":\"smtp-duration-by-server\",\"group\":\"smtp.example.com\",\"value\":13.434}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":\"this_is_a_bug\"}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.066}")
        );
        // Means:
        //  /           10.244
        //  /about      11.818
        //  /contact    10.066
    }

    private Iterable<Map<String, Object>> secondMetrics() {
        return Arrays.asList(
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":14.614}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":12.014}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.747}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.774}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.791}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.012}")
        );
        // Means:
        //  /           12.393
        //  /about      10.591
        //  /contact    10.012
    }

    private Iterable<Map<String, Object>> thirdMetrics() {
        return Arrays.asList(
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.26}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":12.129}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.292}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.702}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.35}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.649}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.535}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.087}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.65}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.116}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":13.118}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.383}")
        );
        // Means:
        //  /           11.305333333
        //  /about      10.184333333
        //  /contact    10.628666667
    }

    private Iterable<Map<String, Object>> fourthMetrics() {
        return Arrays.asList(
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.203}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":8.861}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.57}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":8.878}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.088}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.741}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.088}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.029}"),
                parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":9.224}")
        );
        // Means:
        //  /           9.67525
        //  /about      9.345666667
        //  /contact    10.472
    }
}
