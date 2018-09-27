package it.iks.openapm.jobs.metrics;

import it.iks.openapm.models.MetricConfig;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;

import java.util.Date;

/**
 * Base metric step component fetching needed parameters from job context
 * before execution.
 */
abstract public class MetricStepComponent {
    /**
     * Configuration to know where to fetch metrics from
     */
    protected MetricConfig config;

    /**
     * Job startup time to determine fetch interval
     */
    protected Date startupTime;

    /**
     * Load configurations from job parameters/context into local variables
     * before step starts.
     *
     * @param stepExecution StepExecution context to extract data from
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        startupTime = stepExecution.getJobParameters().getDate("startup_time");
        config = (MetricConfig) stepExecution.getJobExecution().getExecutionContext().get("config");
    }
}
