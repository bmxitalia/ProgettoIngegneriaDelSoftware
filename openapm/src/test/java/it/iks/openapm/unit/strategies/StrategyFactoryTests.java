package it.iks.openapm.unit.strategies;

import it.iks.openapm.strategies.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyFactoryTests {
    @Autowired
    protected StrategyFactory factory;

    @Test
    public void it_makes_a_strategy_by_identifier() {
        assertThat(factory.byIdentifier("daily")).isInstanceOf(DailyStrategy.class);
        assertThat(factory.byIdentifier("weekly")).isInstanceOf(WeeklyStrategy.class);
        assertThat(factory.byIdentifier("monthly")).isInstanceOf(MonthlyStrategy.class);
    }

    @Test
    public void it_throws_exception_if_strategy_does_not_exists() {
        assertThatExceptionOfType(MissingStrategyException.class)
                .isThrownBy(() -> factory.byIdentifier("not_found"));
    }
}
