package it.iks.openapm.jobs.baselines;

import it.iks.openapm.jobs.MissingConfigurationException;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.repositories.BaselineConfigRepository;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Optional;

/**
 * Fetch baseline generation configuration from database using ID in job parameters
 */
public class FetchBaselineConfigTasklet implements Tasklet {
    /**
     * BaselineConfig repository to fetch needed configurations
     */
    private final BaselineConfigRepository repository;

    /**
     * Create instance of tasklet
     *
     * @param repository BaselineConfig repository to fetch needed configurations
     */
    public FetchBaselineConfigTasklet(BaselineConfigRepository repository) {
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
    private BaselineConfig fetchConfig(String configId) {
        Optional<BaselineConfig> config = repository.findById(configId);

        if (!config.isPresent()) {
            throw new MissingConfigurationException(
                    String.format("BaselineConfig \"%s\" is not present", configId)
            );
        }

        return config.get();
    }
}
