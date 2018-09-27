package it.iks.openapm.unit.dispatchers;

import it.iks.openapm.dispatchers.BaselineGenerationDispatcher;
import it.iks.openapm.models.BaselineConfig;
import it.iks.openapm.models.Operation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaselineGenerationDispatcherTests {
    @Autowired
    private Job baselineGenerationJob;

    @Test
    public void it_dispatch_job() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        BaselineConfig config = new BaselineConfig(
                "abc123",
                "baseline-1",
                "metrics",
                "baselines",
                "daily",
                new Operation[]{},
                null,
                null
        );

        JobLauncher launcher = mock(JobLauncher.class);
        given(launcher.run(eq(baselineGenerationJob), any(JobParameters.class))).willReturn(null);
        InOrder inOrder = inOrder(launcher);

        BaselineGenerationDispatcher dispatcher = new BaselineGenerationDispatcher()
                .config(config)
                .job(baselineGenerationJob)
                .launcher(launcher);

        dispatcher.run();

        inOrder.verify(launcher).run(
                eq(baselineGenerationJob),
                argThat(parameters -> parameters.getDate("calculation_time").equals(startingTimeOfPreviousHour())
                        && parameters.getString("config_id").equals("abc123"))
        );
    }

    private Date startingTimeOfPreviousHour() {
        return Date.from(
                Instant.now().truncatedTo(ChronoUnit.HOURS).minus(1, ChronoUnit.HOURS)
        );
    }
}
