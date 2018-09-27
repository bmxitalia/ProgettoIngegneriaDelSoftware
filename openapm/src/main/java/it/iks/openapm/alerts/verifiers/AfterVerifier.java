package it.iks.openapm.alerts.verifiers;

import it.iks.openapm.alerts.AlertState;
import it.iks.openapm.models.AlertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Verifier that fire remediation actions after {@code amount} seconds the state changes to {@code AlertState.ACTIVE}
 */
public class AfterVerifier extends AbstractVerifier {
    /**
     * Spring Scheduler to fire delayed events
     */
    private TaskScheduler scheduler;

    /**
     * Scheduled event firing task if started
     */
    private ScheduledFuture<?> scheduledTask;

    /**
     * Instantiate AfterVerifier.
     *
     * @param config Alert configuration
     */
    public AfterVerifier(AlertConfig config) {
        super(config);
    }

    /**
     * Autowire scheduler
     *
     * @param scheduler Spring Scheduler
     */
    @Autowired
    public void setScheduler(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onChange(AlertState newState, AlertState oldState) {
        if (newState.equals(AlertState.ACTIVE) && scheduledTask == null) {
            scheduleTask();
        } else if (newState.equals(AlertState.INACTIVE) && scheduledTask != null) {
            abort();
        }
    }

    /**
     * Schedule event firing after given amount of seconds
     */
    private void scheduleTask() {
        scheduledTask = scheduler.schedule(
                () -> publisher.triggered(config),
                Date.from(Instant.now().plus(config.getVerification().getAmount(), ChronoUnit.SECONDS))
        );
    }

    /**
     * Abort event firing if not running
     */
    private void abort() {
        scheduledTask.cancel(false);
        scheduledTask = null;
    }
}
