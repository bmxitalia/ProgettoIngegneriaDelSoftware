package it.iks.openapm.jobs.metrics;

import it.iks.openapm.jobs.MissingConfigurationException;
import it.iks.openapm.models.MetricConfig;
import it.iks.openapm.repositories.MetricConfigRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Optional;

/**
 * Fetch metric generation configuration from database using ID in job parameters
 */
public class FetchMetricConfigTasklet implements Tasklet {
    /**
     * MetricConfig repository to fetch needed configurations
     */
    private final MetricConfigRepository repository;

    /**
     * Create instance of tasklet
     *
     * @param repository MetricConfig repository to fetch needed configurations
     */
    public FetchMetricConfigTasklet(MetricConfigRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        JobExecution jobExecution = chunkContext.getStepContext().getStepExecution().getJobExecution();
        String configId = jobExecution.getJobParameters().getString("config_id");
        jobExecution.getExecutionContext().put("config", fetchConfig(configId));

        return RepeatStatus.FINISHED;
    }

    /**
     * Fetch configuration from repository
     *
     * @param configId Configuration ID
     * @return Configuration Model
     */
    private MetricConfig fetchConfig(String configId) {
        Optional<MetricConfig> config = repository.findById(configId);

        if (!config.isPresent()) {
            throw new MissingConfigurationException(
                    String.format("MetricConfig \"%s\" is not present", configId)
            );
        }

        return config.get();
    }
}
