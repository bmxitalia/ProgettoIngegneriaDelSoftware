package it.iks.openapm.integration.alerts;

import it.iks.openapm.alerts.Alert;
import it.iks.openapm.alerts.factories.AlertFactory;
import it.iks.openapm.databases.Database;
import it.iks.openapm.models.*;
import it.iks.openapm.publishers.AlertEventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.iks.openapm.TestUtils.parseJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"app.alerts.evaluate.debounce=100"})
public class AlertDebounceTests {
    @MockBean
    @Qualifier("baselinesDatabase")
    private Database baselinesDatabase;
    @Autowired
    private AlertFactory alertFactory;

    private Alert alert() {
        return alertFactory.alert(
                new AlertConfig()
                        .setId("abc123")
                        .setName("alert-1")
                        .setEvaluateWhen("all_match")
                        .setConditions(new ConditionConfig[]{
                                new ConditionConfig()
                                        .setMetrics(new Operation[][]{
                                                new Operation[]{
                                                        new Operation(new String[]{"name", "'http-duration-by-instance'"}, "="),
                                                        new Operation(new String[]{"group", "'/'"}, "=")
                                                },
                                                new Operation[]{
                                                        new Operation(new String[]{"name", "'http-duration-by-instance'"}, "="),
                                                        new Operation(new String[]{"group", "'/contact'"}, "=")
                                                }
                                        })
                                        .setCalculation(new Operation(new String[]{"value"}, "+"))
                                        .setBaseline(new BaselineValue(
                                                "baselines",
                                                new Operation[]{
                                                        new Operation(new String[]{"name", "'baseline-1'"}, "="),
                                                        new Operation(new String[]{"group", "'/'"}, "=")
                                                },
                                                "daily",
                                                new Operation(new String[]{"mean", "deviation"}, "+")
                                        ))
                                        .setEvaluation(new Operation(new String[]{"value", "baseline_value"}, ">"))
                        })
                        .setVerification(new VerificationConfig("immediately", 0))
                        .setActions(new String[]{"action-1", "action-2"})
        );
    }

    private List<Map<String, Object>> baselines() {
        return Arrays.asList(
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/\",\"strategy\":\"daily\",\"mean\":10.91625,\"deviation\":1.0444285369282096,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/about\",\"strategy\":\"daily\",\"mean\":10.557125,\"deviation\":0.9084302499901807,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/contact\",\"strategy\":\"daily\",\"mean\":10.306375,\"deviation\":0.2775435495107024,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/\",\"strategy\":\"daily\",\"mean\":11,\"deviation\":1,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/about\",\"strategy\":\"daily\",\"mean\":12,\"deviation\":2,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/contact\",\"strategy\":\"daily\",\"mean\":13,\"deviation\":3,\"hour\":16}")
        );
    }

    @Test
    public void it_triggers_calculation_only_once() throws InterruptedException {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any())).willReturn(baselines());
        Alert alert = alert();

        alert.evaluateMetric(parseJson("{\"name\":\"http-duration-by-instance\",\"group\":\"/\",\"value\":15}"));
        alert.evaluateMetric(parseJson("{\"name\":\"http-duration-by-instance\",\"group\":\"/contact\",\"value\":15}"));

        // Wait for debounce operation
        Thread.sleep(150);

        // We measure the number of calculation by checking for the most expensive operation: database fetch
        verify(baselinesDatabase, times(1)).findNewestWhere(eq("baselines"), any());
    }
}
