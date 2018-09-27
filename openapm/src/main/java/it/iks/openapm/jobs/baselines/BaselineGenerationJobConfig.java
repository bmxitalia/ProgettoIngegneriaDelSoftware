package it.iks.openapm.jobs.baselines;

import it.iks.openapm.databases.Database;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.repositories.BaselineConfigRepository;
import it.iks.openapm.strategies.StrategyFactory;
import it.iks.openapm.templators.IndexTemplator;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Define job to generate baselines from metrics
 */
@Configuration
public class BaselineGenerationJobConfig {
    /**
     * Spring Batch job builder factory
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * Spring Batch step builder factory
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * BaselineConfig repository to fetch needed configurations
     */
    private final BaselineConfigRepository repository;

    /**
     * Metric database
     */
    private final Database metricsDatabase;

    /**
     * Baseline database
     */
    private final Database baselinesDatabase;

    /**
     * Operator factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * Templator to parse index names
     */
    private final IndexTemplator indexTemplator;

    /**
     * Strategy factory
     */
    private final StrategyFactory strategyFactory;

    /**
     * Baselines type for database which needs it
     */
    @Value("${app.baselines.database.type}")
    private String baselinesType;

    /**
     * Resolve job from IoC
     *
     * @param jobBuilderFactory  Spring Batch job builder factory
     * @param stepBuilderFactory Spring Batch step builder factory
     * @param repository         BaselineConfig repository to fetch needed configurations
     * @param metricsDatabase    Metric database
     * @param baselinesDatabase  Baseline database
     * @param operatorFactory    Operator factory
     * @param indexTemplator     Templator to parse index names
     * @param strategyFactory    Strategy factory
     */
    @Autowired
    public BaselineGenerationJobConfig(JobBuilderFactory jobBuilderFactory,
                                       StepBuilderFactory stepBuilderFactory,
                                       BaselineConfigRepository repository,
                                       Database metricsDatabase,
                                       Database baselinesDatabase,
                                       OperatorFactory operatorFactory,
                                       IndexTemplator indexTemplator,
                                       StrategyFactory strategyFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.repository = repository;
        this.metricsDatabase = metricsDatabase;
        this.baselinesDatabase = baselinesDatabase;
        this.operatorFactory = operatorFactory;
        this.indexTemplator = indexTemplator;
        this.strategyFactory = strategyFactory;
    }

    /**
     * Job to generate baselines following given configurations
     *
     * @return Job
     */
    @Bean
    public Job baselineGenerationJob() {
        return jobBuilderFactory.get("baselineGenerationJob")
                .incrementer(new RunIdIncrementer())
                .flow(baselineGenerationFetchConfigStep())
                .next(baselineGenerationGenerateStep())
                .end()
                .build();
    }

    /**
     * Step to fetch baseline generation configuration from database using
     * ID stored in JobParameters
     *
     * @return Step
     */
    private Step baselineGenerationFetchConfigStep() {
        return stepBuilderFactory.get("baselineGenerationFetchConfigStep")
                .tasklet(fetchConfigTasklet())
                .build();
    }

    private Tasklet fetchConfigTasklet() {
        return new FetchBaselineConfigTasklet(repository);
    }

    /**
     * Step to alert the baselines
     *
     * @return Step
     */
    private Step baselineGenerationGenerateStep() {
        return stepBuilderFactory.get("baselineGenerationGenerateStep")
                .<Iterable<Iterable<Map<String, Object>>>, Iterable<Map<String, Object>>>chunk(1)
                .reader(metricReader())
                .processor(metricProcessor())
                .writer(baselineWrite())
                .build();
    }

    private MetricsReader metricReader() {
        return new MetricsReader(metricsDatabase, indexTemplator, strategyFactory);
    }

    private ItemProcessor<Iterable<Iterable<Map<String, Object>>>, Iterable<Map<String, Object>>> metricProcessor() {
        return new MetricsCompositeProcessor(operatorFactory, strategyFactory);
    }

    private BaselinesWriter baselineWrite() {
        return new BaselinesWriter(baselinesDatabase, indexTemplator, baselinesType);
    }
}
