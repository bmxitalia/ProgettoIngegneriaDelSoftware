package it.iks.openapm.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

/**
 * A simple debouncer implementation
 */
@Component
public class Debouncer {
    private final TaskScheduler scheduler;
    private final ConcurrentMap<Object, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();

    @Autowired
    public Debouncer(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Debounce given task
     *
     * @param key  Call identifier (it will be use as synchronized lock)
     * @param task Task to debounce
     * @param wait Wait time
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public void call(Object key, Runnable task, int wait) {
        if (wait <= 0) {
            task.run();
            return;
        }

        synchronized (key) {
            if (tasks.containsKey(key)) {
                tasks.get(key).cancel(false);
            }

            tasks.put(key, scheduleTask(task, wait));
        }
    }

    private ScheduledFuture<?> scheduleTask(Runnable task, int wait) {
        return scheduler.schedule(task, new Date(System.currentTimeMillis() + wait));
    }
}
