package it.iks.openapm.unit;

import it.iks.openapm.SchedulingConfigurer;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.repositories.BaselineConfigRepository;
import it.iks.openapm.repositories.MetricConfigRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SchedulingConfigurerTests {
    @MockBean
    private MetricConfigRepository metricRepository;

    @MockBean
    private BaselineConfigRepository baselineRepository;

    @Autowired
    private SchedulingConfigurer configurer;

    @Test
    public void it_does_not_set_tasks_with_no_config() {
        given(metricRepository.findAll()).willReturn(new ArrayList<>());
        given(baselineRepository.findAll()).willReturn(new ArrayList<>());

        ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

        configurer.configureTasks(taskRegistrar);

        assertThat(taskRegistrar.getCronTaskList()).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void it_configure_metric_generation_tasks() {
        given(metricRepository.findAll()).willReturn(Arrays.asList(
                new MetricConfig()
                        .setName("metric-1")
                        .setCron("0 * * * * *"), // Every Minute
                new MetricConfig()
                        .setName("metric-2")
                        .setCron("0 15 14 1 * *") // At 14:15 on day-of-month 1.
        ));
        given(baselineRepository.findAll()).willReturn(Arrays.asList(
                new BaselineConfig()
                        .setName("baseline-1")
        ));

        ScheduledTaskRegistrar taskRegistrar = new ScheduledTaskRegistrar();

        configurer.configureTasks(taskRegistrar);

        assertThat(taskRegistrar.getCronTaskList()).hasSize(3);
        assertThat(taskRegistrar.getCronTaskList()).anyMatch((task) -> task.getExpression().equals("0 * * * * *"));
        assertThat(taskRegistrar.getCronTaskList()).anyMatch((task) -> task.getExpression().equals("0 15 14 1 * *"));
        assertThat(taskRegistrar.getCronTaskList()).anyMatch((task) -> task.getExpression().equals("0 0 * * * *"));
    }
}
