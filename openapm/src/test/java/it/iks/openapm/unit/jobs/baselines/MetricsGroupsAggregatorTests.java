package it.iks.openapm.unit.jobs.baselines;

import it.iks.openapm.jobs.baselines.MetricsGroupsAggregator;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.Operation;
import it.iks.openapm.operators.OperatorFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static it.iks.openapm.TestUtils.parseJson;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricsGroupsAggregatorTests {
    @Autowired
    private OperatorFactory operatorFactory;

    @Test
    public void it_aggregates_metrics_periods_by_group() {
        MetricsGroupsAggregator processor = new MetricsGroupsAggregator(
                new BaselineConfig().setAggregation(new Operation(new String[]{"group"})),
                operatorFactory
        );

        Map<String, List<List<Map<String, Object>>>> result = processor.process(input());
        assertThat(result).isEqualTo(output());
    }

    private Iterable<Iterable<Map<String, Object>>> input() {
        return Arrays.asList(
                Arrays.asList(
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.307}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.818}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.181}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.066}")
                ),
                Arrays.asList(
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":14.614}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":12.014}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.747}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.774}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.791}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.012}")
                ),
                Arrays.asList(
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.26}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.702}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.35}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.649}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.087}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.65}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.116}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":13.118}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.383}")
                ),
                Arrays.asList(
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.203}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":8.861}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.57}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":8.878}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.088}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.741}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.088}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.029}"),
                        parseJson("{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":9.224}")
                )
        );
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<List<Map<String, Object>>>> output() {
        return (Map<String, List<List<Map<String, Object>>>>) (Map<String, ?>) parseJson("{" +
                "\"/\": [" +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.307}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.181}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":14.614}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.774}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.791}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":11.26}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.65}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":13.118}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.383}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.57}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":8.878}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":10.029}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/\",\"value\":9.224}" +
                "]" +
                "]," +

                "\"/about\": [" +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.818}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":12.014}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.747}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.35}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.087}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":11.116}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":8.861}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":9.088}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/about\",\"value\":10.088}" +
                "]" +
                "]," +

                "\"/contact\": [" +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.066}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":10.012}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.702}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.649}" +
                "]," +
                "[" +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":11.203}," +
                "{\"name\":\"http-duration-by-page\",\"group\":\"/contact\",\"value\":9.741}" +
                "]" +
                "]" +
                "}");
    }
}
