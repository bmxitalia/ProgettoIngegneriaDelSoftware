package it.iks.openapm.unit.jobs.baselines;

import it.iks.openapm.jobs.baselines.BaselinesCalculator;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.strategies.StrategyFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static it.iks.openapm.TestUtils.parseJson;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaselinesCalculatorTests {
    @Autowired
    private StrategyFactory strategyFactory;

    @Autowired
    private OperatorFactory operatorFactory;

    @SuppressWarnings("unchecked")
    private Map<String, List<Iterable<Map<String, Object>>>> input() {
        return (Map<String, List<Iterable<Map<String, Object>>>>) (Map<String, ?>) parseJson("{" +
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

    @Test
    public void it_calculates_daily_baselines() {
        BaselinesCalculator processor = new BaselinesCalculator(
                new BaselineConfig().setName("baseline-1").setStrategy("daily").setCalculationField("value"),
                new Date(1516028400000L), // 2018-01-15 16.00
                operatorFactory,
                strategyFactory
        );

        Iterable<Map<String, Object>> result = processor.process(input());
        assertThat(result).isEqualTo(outputDaily());
    }

    private Iterable<Map<String, Object>> outputDaily() {
        return Arrays.asList(
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/\",\"strategy\":\"daily\",\"mean\":10.91625,\"deviation\":1.0444285369282096,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/about\",\"strategy\":\"daily\",\"mean\":10.557125,\"deviation\":0.9084302499901807,\"hour\":16}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/contact\",\"strategy\":\"daily\",\"mean\":10.306375,\"deviation\":0.2775435495107024,\"hour\":16}")
        );
    }

    @Test
    public void it_calculates_weekly_baselines() {
        BaselinesCalculator processor = new BaselinesCalculator(
                new BaselineConfig().setName("baseline-1").setStrategy("weekly").setCalculationField("value"),
                new Date(1516028400000L), // 2018-01-15 16.00
                operatorFactory,
                strategyFactory
        );

        Iterable<Map<String, Object>> result = processor.process(input());
        assertThat(result).isEqualTo(outputWeekly());
    }

    private Iterable<Map<String, Object>> outputWeekly() {
        return Arrays.asList(
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/\",\"strategy\":\"weekly\",\"mean\":10.91625,\"deviation\":1.0444285369282096,\"hour\":16,\"day_of_week\":1}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/about\",\"strategy\":\"weekly\",\"mean\":10.557125,\"deviation\":0.9084302499901807,\"hour\":16,\"day_of_week\":1}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/contact\",\"strategy\":\"weekly\",\"mean\":10.306375,\"deviation\":0.2775435495107024,\"hour\":16,\"day_of_week\":1}")
        );
    }

    @Test
    public void it_calculates_monthly_baselines() {
        BaselinesCalculator processor = new BaselinesCalculator(
                new BaselineConfig().setName("baseline-1").setStrategy("monthly").setCalculationField("value"),
                new Date(1516028400000L), // 2018-01-15 16.00
                operatorFactory,
                strategyFactory
        );

        Iterable<Map<String, Object>> result = processor.process(input());
        assertThat(result).isEqualTo(outputMonthly());
    }

    private Iterable<Map<String, Object>> outputMonthly() {
        return Arrays.asList(
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/\",\"strategy\":\"monthly\",\"mean\":10.91625,\"deviation\":1.0444285369282096,\"hour\":16,\"day_of_month\":15}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/about\",\"strategy\":\"monthly\",\"mean\":10.557125,\"deviation\":0.9084302499901807,\"hour\":16,\"day_of_month\":15}"),
                parseJson("{\"name\":\"baseline-1\",\"group\":\"/contact\",\"strategy\":\"monthly\",\"mean\":10.306375,\"deviation\":0.2775435495107024,\"hour\":16,\"day_of_month\":15}")
        );
    }
}
