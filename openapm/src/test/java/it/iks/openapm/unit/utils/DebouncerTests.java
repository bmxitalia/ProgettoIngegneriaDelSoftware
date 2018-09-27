package it.iks.openapm.unit.utils;

import it.iks.openapm.utils.Debouncer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class DebouncerTests {
    @Autowired
    private Debouncer debouncer;

    @Test
    public void it_debounce_calls() throws InterruptedException {
        Runnable task = mock(Runnable.class);
        Object lock = new Object();

        debouncer.call(lock, task, 10);
        debouncer.call(lock, task, 10);
        debouncer.call(lock, task, 10);
        debouncer.call(lock, task, 10);
        Thread.sleep(20);

        verify(task, times(1)).run();
    }

    @Test
    public void it_debounce_calls_on_lock() throws InterruptedException {
        Runnable task = mock(Runnable.class);
        Object lock1 = new Object();
        Object lock2 = new Object();

        debouncer.call(lock1, task, 10);
        debouncer.call(lock2, task, 10);
        debouncer.call(lock1, task, 10);
        debouncer.call(lock2, task, 10);
        Thread.sleep(20);

        verify(task, times(2)).run();
    }

    @Test
    public void it_execute_synchronously_if_wait_is_zero() {
        Runnable task = mock(Runnable.class);
        Object lock = new Object();

        debouncer.call(lock, task, 0);
        debouncer.call(lock, task, 0);
        debouncer.call(lock, task, 0);
        debouncer.call(lock, task, 0);

        verify(task, times(4)).run();
    }
}
