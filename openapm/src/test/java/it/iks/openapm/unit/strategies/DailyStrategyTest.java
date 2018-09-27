package it.iks.openapm.unit.strategies;

import it.iks.openapm.strategies.DailyStrategy;
import it.iks.openapm.strategies.Strategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class DailyStrategyTest {

    @Test
    public void it_determines_periods() {
        Strategy strategy = new DailyStrategy(4);

        assertThat(strategy.periods(new Date(1484060400000L))) // 2017-01-10 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1483801200000L), // 2017-01-07 15.00
                        new Date(1483887600000L), // 2017-01-08 15.00
                        new Date(1483974000000L), // 2017-01-09 15.00
                        new Date(1484060400000L)  // 2017-01-10 15.00
                ));
    }

    @Test
    public void it_determines_periods_cross_month() {
        Strategy strategy = new DailyStrategy(4);

        assertThat(strategy.periods(new Date(1486047600000L))) // 2017-02-02 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1485788400000L), // 2017-01-30 15.00
                        new Date(1485874800000L), // 2017-01-31 15.00
                        new Date(1485961200000L), // 2017-02-01 15.00
                        new Date(1486047600000L)  // 2017-02-02 15.00
                ));
    }

    @Test
    public void it_determines_periods_cross_year() {
        Strategy strategy = new DailyStrategy(4);

        assertThat(strategy.periods(new Date(1483369200000L))) // 2017-01-02 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1483110000000L), // 2016-12-30 15.00
                        new Date(1483196400000L), // 2016-12-31 15.00
                        new Date(1483282800000L), // 2017-01-01 15.00
                        new Date(1483369200000L)  // 2017-01-02 15.00
                ));
    }

    @Test
    public void it_determines_two_periods() {
        Strategy strategy = new DailyStrategy(2);

        assertThat(strategy.periods(new Date(1484060400000L))) // 2017-01-10 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1483974000000L), // 2017-01-09 15.00
                        new Date(1484060400000L)  // 2017-01-10 15.00
                ));
    }

    @Test
    public void it_builds_attributes_map() {
        Strategy strategy = new DailyStrategy(2);

        Map<String, Object> expectedAttributes = new HashMap<>();
        expectedAttributes.put("strategy", "daily");
        expectedAttributes.put("hour", 16);

        assertThat(strategy.attributes(new Date(1484060400000L))) // 2017-01-10 16.00
                .isEqualTo(expectedAttributes);
    }

    @Test
    public void it_calculates_previous_period() {
        Strategy strategy = new DailyStrategy(2);

        assertThat(strategy.previousPeriod(new Date(1484061330000L))) // 2017-01-10 15.15.30
                .isEqualTo(new Date(1483974000000L)); // 2017-01-09 15.00
    }
}
