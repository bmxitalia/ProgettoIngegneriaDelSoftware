package it.iks.openapm;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication(exclude = {ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
@EnableAsync
@EnableBatchProcessing
public class OpenapmApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenapmApplication.class, args);
    }

    /**
     * Define a task executor to be used in the application
     *
     * @return Task Executor
     */
    @Bean
    public TaskExecutor taskExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cores);
        executor.setThreadNamePrefix("executor-");
        executor.afterPropertiesSet();
        return executor;
    }

    /**
     * Define a job launcher to be used in the application
     *
     * @return Job Launcher
     */
    @Bean
    public JobLauncher jobLauncher(TaskExecutor taskExecutor, JobRepository jobRepository) {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setTaskExecutor(taskExecutor);
        launcher.setJobRepository(jobRepository);
        return launcher;
    }
}
