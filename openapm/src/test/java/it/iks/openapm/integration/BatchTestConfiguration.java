package it.iks.openapm.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;

@Configuration
public class BatchTestConfiguration {
    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils() {
            /**
             * Override method to avoid autowire trying to fill Job instance
             * and throwing NoUniqueBeanDefinitionException.
             *
             * @param job
             */
            @Override
            public void setJob(Job job) {
                super.setJob(job);
            }

            /**
             * Set a sync task executor to simplify integration test (no need to wait for results, etc)
             *
             * @param jobLauncher
             */
            @Autowired
            @Override
            public void setJobLauncher(JobLauncher jobLauncher) {
                if (jobLauncher instanceof SimpleJobLauncher) {
                    ((SimpleJobLauncher) jobLauncher).setTaskExecutor(new SyncTaskExecutor());
                }

                super.setJobLauncher(jobLauncher);
            }
        };
    }
}
