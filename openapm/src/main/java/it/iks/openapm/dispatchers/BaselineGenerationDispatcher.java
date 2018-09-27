package it.iks.openapm.dispatchers;

import it.iks.openapm.models.BaselineConfig;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Dispatcher for baseline generation jobs based on last hour
 */
public class BaselineGenerationDispatcher implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(BaselineGenerationDispatcher.class);

    /**
     * Baseline configuration to alert generation job
     */
    private BaselineConfig config;

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
     * @param config Baseline generation configuration
     * @return this
     */
    public BaselineGenerationDispatcher config(BaselineConfig config) {
        this.config = config;
        return this;
    }

    /**
     * Set Spring Batch launcher
     *
     * @param jobLauncher Spring Batch job launcher instance
     * @return this
     */
    public BaselineGenerationDispatcher launcher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
        return this;
    }

    /**
     * Set Spring Batch job instance
     *
     * @param job Job instance
     * @return this
     */
    public BaselineGenerationDispatcher job(Job job) {
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

            log.info(String.format("Dispatched baseline generation job \"%s\"", config.getName()));
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            log.error(String.format(
                    "Cannot dispatch baseline generation job \"%s\"",
                    config.getName()
            ), e);
        }
    }

    private JobParameters parameters() {
        Map<String, JobParameter> parameters = new HashMap<>();

        parameters.put("calculation_time", calculationTime());
        parameters.put("config_id", new JobParameter(config.getId()));

        return new JobParameters(parameters);
    }

    private JobParameter calculationTime() {
        return new JobParameter(Date.from(
                Instant.now().truncatedTo(ChronoUnit.HOURS).minus(1, ChronoUnit.HOURS)
        ));
    }
}
