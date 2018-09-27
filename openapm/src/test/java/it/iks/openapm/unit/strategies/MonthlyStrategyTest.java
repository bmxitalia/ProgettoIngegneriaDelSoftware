package it.iks.openapm.unit.strategies;

import it.iks.openapm.strategies.MonthlyStrategy;
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
public class MonthlyStrategyTest {

    @Test
    public void it_determines_periods() {
        Strategy strategy = new MonthlyStrategy(4);

        assertThat(strategy.periods(new Date(1531666800000L))) // 2018-07-15 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1523804400000L), // 2018-04-15 15.00
                        new Date(1526396400000L), // 2018-05-15 15.00
                        new Date(1529074800000L), // 2018-06-15 15.00
                        new Date(1531666800000L)  // 2018-07-15 15.00
                ));
    }

    @Test
    public void it_determines_periods_cross_year() {
        Strategy strategy = new MonthlyStrategy(4);

        assertThat(strategy.periods(new Date(1521126000000L))) // 2018-03-15 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1513350000000L), // 2017-12-15 15.00
                        new Date(1516028400000L), // 2018-01-15 15.00
                        new Date(1518706800000L), // 2018-02-15 15.00
                        new Date(1521126000000L)  // 2018-03-15 15.00
                ));
    }

    @Test
    public void it_determines_periods_for_edge_days() {
        Strategy strategy = new MonthlyStrategy(4);

        assertThat(strategy.periods(new Date(1535727600000L))) // 2018-08-31 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1522508400000L), // 2018-03-31 15.00
                        new Date(1527778800000L), // 2018-05-31 15.00
                        new Date(1533049200000L), // 2018-07-31 15.00
                        new Date(1535727600000L)  // 2018-08-31 15.00
                ));
    }

    @Test
    public void it_determines_periods_for_edge_days_of_february() {
        Strategy strategy = new MonthlyStrategy(4);

        assertThat(strategy.periods(new Date(1527606000000L))) // 2018-05-29 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1517241600000L), // 2018-01-29 16.00
                        new Date(1522335600000L), // 2018-03-29 15.00
                        new Date(1525014000000L), // 2018-04-29 15.00
                        new Date(1527606000000L)  // 2018-05-29 15.00
                ));
    }

    @Test
    public void it_determines_two_periods() {
        Strategy strategy = new MonthlyStrategy(2);

        assertThat(strategy.periods(new Date(1529074800000L))) // 2018-06-15 15.00
                .isEqualTo(Arrays.asList(
                        new Date(1526396400000L), // 2018-05-15 15.00
                        new Date(1529074800000L)  // 2018-06-15 15.00
                ));
    }

    @Test
    public void it_builds_attributes_map() {
        Strategy strategy = new MonthlyStrategy(2);

        Map<String, Object> expectedAttributes = new HashMap<>();
        expectedAttributes.put("strategy", "monthly");
        expectedAttributes.put("hour", 16);
        expectedAttributes.put("day_of_month", 10);

        assertThat(strategy.attributes(new Date(1484060400000L))) // 2017-01-10 16.00
                .isEqualTo(expectedAttributes);
    }

    @Test
    public void it_calculates_previous_period() {
        Strategy strategy = new MonthlyStrategy(2);

        assertThat(strategy.previousPeriod(new Date(1484061330000L))) // 2017-01-10 15.15.30
                .isEqualTo(new Date(1481382000000L)); // 2016-12-10 15.00
    }
}
