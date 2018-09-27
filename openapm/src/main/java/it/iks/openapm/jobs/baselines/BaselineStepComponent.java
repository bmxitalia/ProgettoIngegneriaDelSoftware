package it.iks.openapm.jobs.baselines;

import it.iks.openapm.models.BaselineConfig;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;

import java.util.Date;

/**
 * Base baseline generation step component fetching needed
 * parameters from job context before execution.
 */
abstract public class BaselineStepComponent {
    /**
     * Configuration to know where to fetch metrics from
     */
    protected BaselineConfig config;

    /**
     * Job calculation time to determine fetch interval
     */
    protected Date calculationTime;

    /**
     * Load configurations from job parameters/context into local variables
     * before step starts.
     *
     * @param stepExecution StepExecution context to extract data from
     */
    @BeforeStep
    public void beforeStep(StepExecution stepExecution) {
        calculationTime = stepExecution.getJobParameters().getDate("calculation_time");
        config = (BaselineConfig) stepExecution.getJobExecution().getExecutionContext().get("config");
    }
}
