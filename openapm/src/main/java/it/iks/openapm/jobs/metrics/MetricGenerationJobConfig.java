package it.iks.openapm.jobs.metrics;

import it.iks.openapm.databases.Database;
import it.iks.openapm.operators.OperatorFactory;
import it.iks.openapm.publishers.MetricEventPublisher;
import it.iks.openapm.repositories.MetricConfigRepository;
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
 * Define job to generate metrics from traces
 */
@Configuration
public class MetricGenerationJobConfig {
    /**
     * Spring Batch job builder factory
     */
    private final JobBuilderFactory jobBuilderFactory;

    /**
     * Spring Batch step builder factory
     */
    private final StepBuilderFactory stepBuilderFactory;

    /**
     * MetricConfig repository to fetch needed configurations
     */
    private final MetricConfigRepository repository;

    /**
     * Metric database
     */
    private final Database metricsDatabase;

    /**
     * Traces database
     */
    private final Database tracesDatabase;

    /**
     * Operator factory
     */
    private final OperatorFactory operatorFactory;

    /**
     * Templator to parse index names
     */
    private final IndexTemplator indexTemplator;

    /**
     * Metric events eventPublisher
     */
    private final MetricEventPublisher eventPublisher;

    /**
     * Metrics type for database which needs it
     */
    @Value("${app.metrics.database.type}")
    private String metricsType;

    /**
     * Resolve job from IoC
     *
     * @param jobBuilderFactory  Spring Batch job builder factory
     * @param stepBuilderFactory Spring Batch step builder factory
     * @param repository         MetricConfig repository to fetch needed configurations
     * @param metricsDatabase    Metric database
     * @param tracesDatabase     Traces database
     * @param operatorFactory    Operator factory
     * @param indexTemplator     Templator to parse index names
     * @param publisher          Metric events eventPublisher
     */
    @Autowired
    public MetricGenerationJobConfig(JobBuilderFactory jobBuilderFactory,
                                     StepBuilderFactory stepBuilderFactory,
                                     MetricConfigRepository repository,
                                     Database metricsDatabase,
                                     Database tracesDatabase,
                                     OperatorFactory operatorFactory,
                                     IndexTemplator indexTemplator,
                                     MetricEventPublisher publisher) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.repository = repository;
        this.metricsDatabase = metricsDatabase;
        this.tracesDatabase = tracesDatabase;
        this.operatorFactory = operatorFactory;
        this.indexTemplator = indexTemplator;
        this.eventPublisher = publisher;
    }

    /**
     * Job to generate metrics following given configurations
     *
     * @return Job
     */
    @Bean
    public Job metricGenerationJob() {
        return jobBuilderFactory.get("metricGenerationJob")
                .incrementer(new RunIdIncrementer())
                .flow(metricGenerationFetchConfigStep())
                .next(metricGenerationGenerateStep())
                .end()
                .build();
    }

    /**
     * Step to fetch metric generation configuration from database using
     * ID stored in JobParameters
     *
     * @return Step
     */
    private Step metricGenerationFetchConfigStep() {
        return stepBuilderFactory.get("metricGenerationFetchConfigStep")
                .tasklet(fetchConfigTasklet())
                .build();
    }

    private Tasklet fetchConfigTasklet() {
        return new FetchMetricConfigTasklet(repository);
    }

    /**
     * Step to alert the metrics
     *
     * @return Step
     */
    private Step metricGenerationGenerateStep() {
        return stepBuilderFactory.get("metricGenerationGenerateStep")
                .<Iterable<Map<String, Object>>, Iterable<Map<String, Object>>>chunk(1)
                .reader(traceReader())
                .processor(traceProcessor())
                .writer(metricWriter())
                .build();
    }

    private TracesReader traceReader() {
        return new TracesReader(tracesDatabase, indexTemplator);
    }

    private ItemProcessor<Iterable<Map<String, Object>>, Iterable<Map<String, Object>>> traceProcessor() {
        return new TracesCompositeProcessor(operatorFactory);
    }

    private MetricsWriter metricWriter() {
        return new MetricsWriter(metricsDatabase, indexTemplator, eventPublisher, metricsType);
    }
}
