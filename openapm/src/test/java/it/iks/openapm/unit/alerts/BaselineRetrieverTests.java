package it.iks.openapm.unit.alerts;

import it.iks.openapm.alerts.BaselineRetriever;
import it.iks.openapm.alerts.exceptions.BaselineNotFoundException;
import it.iks.openapm.alerts.factories.AlertFactory;
import it.iks.openapm.databases.Database;
import it.iks.openapm.models.BaselineValue;
import it.iks.openapm.models.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static it.iks.openapm.TestUtils.parseJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class BaselineRetrieverTests {
    @MockBean
    @Qualifier("baselinesDatabase")
    private Database baselinesDatabase;

    @Autowired
    private AlertFactory alertFactory;

    @Test
    public void it_throws_exception_if_no_baseline_is_found() {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any())).willReturn(new ArrayList<>());
        BaselineRetriever retriever = alertFactory.baselineRetriever(
                new BaselineValue("baselines", new Operation[]{}, "daily", new Operation())
        );

        assertThatExceptionOfType(BaselineNotFoundException.class)
                .isThrownBy(() -> retriever.value(new Date()));
    }

    @Test
    public void it_returns_mean_if_one_baseline_is_found() throws BaselineNotFoundException {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any()))
                .willReturn(Collections.singletonList(baselines().get(0)));
        BaselineRetriever retriever = alertFactory.baselineRetriever(
                new BaselineValue(
                        "baselines",
                        new Operation[]{
                                new Operation(new String[]{"name", "'baseline-1'"}, "="),
                                new Operation(new String[]{"group", "'/'"}, "=")
                        },
                        "daily",
                        new Operation(new String[]{"mean"})
                )
        );

        assertThat(retriever.value(new Date(1514822400000L))).isEqualTo(10.91625);
    }

    @Test
    public void it_returns_mean_plus_deviation_if_one_baseline_is_found() throws BaselineNotFoundException {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any()))
                .willReturn(Collections.singletonList(baselines().get(0)));
        BaselineRetriever retriever = alertFactory.baselineRetriever(
                new BaselineValue(
                        "baselines",
                        new Operation[]{
                                new Operation(new String[]{"name", "'baseline-1'"}, "="),
                                new Operation(new String[]{"group", "'/'"}, "=")
                        },
                        "daily",
                        new Operation(new String[]{"mean", "deviation"}, "+")
                )
        );

        assertThat(retriever.value(new Date(1514822400000L))).isEqualTo(10.91625 + 1.0444285369282096);
    }

    @Test
    public void it_returns_mean() throws BaselineNotFoundException {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any())).willReturn(baselines());
        BaselineRetriever retriever = alertFactory.baselineRetriever(
                new BaselineValue(
                        "baselines",
                        new Operation[]{
                                new Operation(new String[]{"name", "'baseline-1'"}, "="),
                                new Operation(new String[]{"group", "'/about'"}, "=")
                        },
                        "daily",
                        new Operation(new String[]{"mean"})
                )
        );

        assertThat(retriever.value(new Date(1514822400000L))).isEqualTo(10.557125);
    }

    @Test
    public void it_returns_mean_plus_deviation() throws BaselineNotFoundException {
        given(baselinesDatabase.findNewestWhere(eq("baselines"), any())).willReturn(baselines());
        BaselineRetriever retriever = alertFactory.baselineRetriever(
                new BaselineValue(
                        "baselines",
                        new Operation[]{
                                new Operation(new String[]{"name", "'baseline-1'"}, "="),
                                new Operation(new String[]{"group", "'/contact'"}, "=")
                        },
                        "daily",
                        new Operation(new String[]{"mean", "deviation"}, "+")
                )
        );

        assertThat(retriever.value(new Date(1514822400000L))).isEqualTo(10.306375 + 0.2775435495107024);
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
}
