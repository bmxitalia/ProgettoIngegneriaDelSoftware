package it.iks.openapm.unit.strategies;

import it.iks.openapm.strategies.Strategy;
import it.iks.openapm.strategies.WeeklyStrategy;
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
public class WeeklyStrategyTest {

    @Test
    public void it_determines_periods() {
        Strategy strategy = new WeeklyStrategy(4);

        assertThat(strategy.periods(new Date(1527865200000L))) // Friday 2018-06-01 15.00/16.00
                .isEqualTo(Arrays.asList(
                        new Date(1526050800000L), // Friday 2018-05-1 15.00/16.00
                        new Date(1526655600000L), // Friday 2018-05-18 15.00/16.00
                        new Date(1527260400000L), // Friday 2018-05-25 15.00/16.00
                        new Date(1527865200000L)  // Friday 2018-06-01 15.00/16.00
                ));
    }

    @Test
    public void it_determines_periods_cross_year() {
        Strategy strategy = new WeeklyStrategy(4);

        assertThat(strategy.periods(new Date(1514905200000L))) // Tuesday 2018-01-02 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1513090800000L), // Tuesday 2017-12-12 15.00
                        new Date(1513695600000L), // Tuesday 2017-12-19 15.00
                        new Date(1514300400000L), // Tuesday 2017-12-26 15.00
                        new Date(1514905200000L)  // Tuesday 2018-01-02 15.00
                ));
    }

    @Test
    public void it_determines_two_periods() {
        Strategy strategy = new WeeklyStrategy(2);

        assertThat(strategy.periods(new Date(1527865200000L))) // Friday 2018-06-01 15.00/16.00
                .isEqualTo(Arrays.asList(
                        new Date(1527260400000L), // Friday 2018-05-25 15.00/16.00
                        new Date(1527865200000L)  // Friday 2018-06-01 15.00/16.00
                ));
    }

    @Test
    public void it_builds_attributes_map() {
        Strategy strategy = new WeeklyStrategy(2);

        Map<String, Object> expectedAttributes = new HashMap<>();
        expectedAttributes.put("strategy", "weekly");
        expectedAttributes.put("hour", 16);
        expectedAttributes.put("day_of_week", 2);

        assertThat(strategy.attributes(new Date(1484060400000L))) // Tuesday 2017-01-10 16.00
                .isEqualTo(expectedAttributes);
    }

    @Test
    public void it_calculates_previous_period() {
        Strategy strategy = new WeeklyStrategy(2);

        assertThat(strategy.previousPeriod(new Date(1484061330000L))) // 2017-01-10 15.15.30
                .isEqualTo(new Date(1483455600000L)); // 2017-01-03 15.00
    }
}
