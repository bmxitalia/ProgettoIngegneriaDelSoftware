package it.iks.openapm.unit.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;
import it.iks.openapm.alerts.verifiers.Verifier;
import it.iks.openapm.alerts.factories.VerifierFactory;
import it.iks.openapm.models.AlertConfig;
import it.iks.openapm.models.VerificationConfig;
import it.iks.openapm.publishers.AlertEventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class AfterVerifierTests {
    @MockBean
    private AlertEventPublisher publisher;

    @MockBean
    private TaskScheduler scheduler;

    @Autowired
    private VerifierFactory factory;

    private AlertConfig config() {
        AlertConfig config = new AlertConfig();
        config.setVerification(new VerificationConfig("after", 300));
        return config;
    }

    @Test
    public void it_queues_event_firing_on_active() {
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);

        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);

        verify(scheduler).schedule(
                any(),
                ArgumentMatchers.<Date>argThat(date ->
                        // 200 ms of calculation time between initial call and evaluation
                        // should be more than enough, especially because delay granularity
                        // is in seconds
                        (date.getTime() - System.currentTimeMillis()) <= 300_000
                                && (date.getTime() - System.currentTimeMillis()) > 299_800
                )
        );
    }

    @Test
    public void it_does_not_queue_event_firing_on_inactive() {
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);

        verifier.onChange(AlertState.INACTIVE, AlertState.ACTIVE);

        verify(scheduler, never()).schedule(any(), any(Date.class));
    }

    @Test
    public void it_does_not_queues_event_firing_twice() {
        given(scheduler.schedule(any(), any(Date.class))).willReturn(mock(ScheduledFuture.class));
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);

        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);
        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);

        verify(scheduler, times(1)).schedule(any(), any(Date.class));
    }

    @Test
    public void it_aborts_queued_event_firing() {
        ScheduledFuture future = mock(ScheduledFuture.class);
        given(scheduler.schedule(any(), any(Date.class))).willReturn(future);
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);

        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);
        verifier.onChange(AlertState.INACTIVE, AlertState.ACTIVE);

        verify(scheduler, times(1)).schedule(any(), any(Date.class));
        verify(future, times(1)).cancel(eq(false));
    }

    @Test
    public void queued_event_firing_does_fire_event() {
        ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        AlertConfig config = config();
        Verifier verifier = factory.forAlert(config);

        verifier.onChange(AlertState.ACTIVE, AlertState.INACTIVE);

        verify(scheduler, times(1)).schedule(runnableCaptor.capture(), any(Date.class));

        runnableCaptor.getValue().run();

        verify(publisher, times(1)).triggered(eq(config));
    }
}
