package it.iks.openapm.dispatchers;

import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.MetricConfig;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

/**
 * Build a metric generation dispatcher from config
 */
@Component
public class DispatchersFactory {
    /**
     * Spring Batch metricJob launcher instance
     */
    private JobLauncher launcher;

    /**
     * Metric generation job instance
     */
    private Job metricJob;

    /**
     * Baseline generation job instance
     */
    private Job baselineJob;

    /**
     * Resolve dispatcher form IoC
     *
     * @param jobLauncher           Spring Batch metricJob launcher instance
     * @param metricGenerationJob   Metric generation job instance
     * @param baselineGenerationJob Baseline generation job instance
     */
    @Autowired
    public DispatchersFactory(JobLauncher jobLauncher, Job metricGenerationJob, Job baselineGenerationJob) {
        this.launcher = jobLauncher;
        this.metricJob = metricGenerationJob;
        this.baselineJob = baselineGenerationJob;
    }

    /**
     * Generate scheduler metric from config model to dispatch jobs
     *
     * @param config Configuration model
     * @return Task for scheduler
     */
    public CronTask metric(MetricConfig config) {
        return new CronTask(buildMetricDispatcher(config), config.getCron());
    }

    private MetricGenerationDispatcher buildMetricDispatcher(MetricConfig config) {
        return new MetricGenerationDispatcher()
                .launcher(launcher)
                .job(metricJob)
                .config(config);
    }

    /**
     * Generate scheduler baseline from config model to dispatch jobs
     *
     * @param config Configuration model
     * @return Task for scheduler
     */
    public CronTask baseline(BaselineConfig config) {
        return new CronTask(buildBaselineDispatcher(config), "0 0 * * * *"); // Every hour
    }

    private BaselineGenerationDispatcher buildBaselineDispatcher(BaselineConfig config) {
        return new BaselineGenerationDispatcher()
                .launcher(launcher)
                .job(baselineJob)
                .config(config);
    }
}
