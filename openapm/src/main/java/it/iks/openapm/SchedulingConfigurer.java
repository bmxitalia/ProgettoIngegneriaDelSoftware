package it.iks.openapm;

import it.iks.openapm.dispatchers.DispatchersFactory;
import it.iks.openapm.repositories.BaselineConfigRepository;
import it.iks.openapm.repositories.MetricConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * Configure Spring Scheduler
 */
@Configuration
@EnableScheduling
public class SchedulingConfigurer implements org.springframework.scheduling.annotation.SchedulingConfigurer {
    private static final Logger log = LoggerFactory.getLogger(SchedulingConfigurer.class);

    /**
     * DispatcherBuilder instance to create tasks
     */
    private final DispatchersFactory dispatchersFactory;

    /**
     * MetricConfig Repository
     */
    private final MetricConfigRepository metricRepository;

    /**
     * BaselineConfig Repository
     */
    private final BaselineConfigRepository baselineRepository;

    /**
     * Resolve configurer from IoC
     *
     * @param dispatchersFactory DispatcherBuilder instance to create tasks
     * @param metricRepository   MetricConfig Repository
     * @param baselineRepository BaselineConfig Repository
     */
    @Autowired
    public SchedulingConfigurer(DispatchersFactory dispatchersFactory,
                                MetricConfigRepository metricRepository,
                                BaselineConfigRepository baselineRepository) {
        this.dispatchersFactory = dispatchersFactory;
        this.metricRepository = metricRepository;
        this.baselineRepository = baselineRepository;
    }

    /**
     * Register tasks for scheduler
     *
     * @param taskRegistrar Task registrar
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        configureMetricTasks(taskRegistrar);
        configureBaselineTasks(taskRegistrar);
    }

    private void configureMetricTasks(ScheduledTaskRegistrar taskRegistrar) {
        metricRepository
                .findAll()
                .forEach(config -> {
                    log.debug(config.toString());

                    taskRegistrar.addCronTask(dispatchersFactory.metric(config));

                    log.info(String.format(
                            "Registered cron metric for metric \"%s\" at \"%s\"",
                            config.getName(),
                            config.getCron()
                    ));
                });
    }

    private void configureBaselineTasks(ScheduledTaskRegistrar taskRegistrar) {
        baselineRepository
                .findAll()
                .forEach(config -> {
                    log.debug(config.toString());

                    taskRegistrar.addCronTask(dispatchersFactory.baseline(config));

                    log.info(String.format(
                            "Registered cron baseline for baseline \"%s\"",
                            config.getName()
                    ));
                });
    }

    /**
     * Define a task scheduler to be used in the application
     *
     * @return Task Scheduler
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setThreadNamePrefix("scheduler-");
        return scheduler;
    }
}
