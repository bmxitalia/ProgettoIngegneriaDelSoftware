package it.iks.openapm.dispatchers;

import it.iks.openapm.models.MetricConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatcher for metric generation jobs based on current time
 */
public class MetricGenerationDispatcher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MetricGenerationDispatcher.class);

    /**
     * Metric configuration to alert generation job
     */
    private MetricConfig config;

    /**
     * Spring Batch job launcher instance
     */
    private JobLauncher jobLauncher;

    /**
     * Job instance
     */
    private Job job;

    /**
     * Set configuration for job launching
     *
     * @param config Metric generation configuration
     * @return this
     */
    public MetricGenerationDispatcher config(MetricConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Set Spring Batch launcher
     *
     * @param jobLauncher Spring Batch job launcher instance
     * @return this
     */
    public MetricGenerationDispatcher launcher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
        return this;
    }

    /**
     * Set Spring Batch job instance
     *
     * @param job Job instance
     * @return this
     */
    public MetricGenerationDispatcher job(Job job) {
        this.job = job;
        return this;
    }

    /**
     * Dispatch generation job to Spring Batch
     */
    @Override
    public void run() {
        try {
            JobParameters parameters = parameters();

            log.debug(parameters.toString());

            jobLauncher.run(job, parameters);

            log.info(String.format("Dispatched metric generation job \"%s\"", config.getName()));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(String.format(
                    "Cannot dispatch metric generation job \"%s\"",
                    config.getName()
            ), e);
        }
    }

    private JobParameters parameters() {
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("startup_time", new JobParameter(new Date()));
        parameters.put("config_id", new JobParameter(config.getId()));

        return new JobParameters(parameters);
    }
}
