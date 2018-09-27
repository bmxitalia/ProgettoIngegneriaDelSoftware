package it.iks.openapm.unit.dispatchers;

import it.iks.openapm.dispatchers.MetricGenerationDispatcher;
import it.iks.openapm.models.MetricConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetricGenerationDispatcherTests {
    @Autowired
    private Job metricGenerationJob;

    @Test
    public void it_dispatch_job() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        MetricConfig config = new MetricConfig("metric-1", "0 * * * * *", "traces", "metrics");
        config.setId("abc123");

        JobLauncher launcher = mock(JobLauncher.class);
        given(launcher.run(eq(metricGenerationJob), any(JobParameters.class))).willReturn(null);

        MetricGenerationDispatcher dispatcher = new MetricGenerationDispatcher()
                .config(config)
                .job(metricGenerationJob)
                .launcher(launcher);

        dispatcher.run();

        verify(launcher, times(1)).run(eq(metricGenerationJob), any(JobParameters.class));
    }
}
