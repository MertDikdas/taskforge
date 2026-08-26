package com.taskforge.worker.job.retry;

import com.taskforge.worker.job.exception.RetryableJobException;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RetryPolicy {

    private static final Duration BASE_DELAY = Duration.ofSeconds(5);

    private static final double MULTIPLIER = 3.0;

    private static final Duration MAX_DELAY = Duration.ofSeconds(45);

    public boolean isRetryable(Throwable exception) {
        return exception instanceof RetryableJobException;
    }

    public Duration delayFor(int retryNumber) {
        double multiplier = Math.pow(
                MULTIPLIER,
                retryNumber - 1
        );
        long millis =(long) (BASE_DELAY.toMillis()* multiplier);
        return Duration.ofMillis(
                Math.min(
                        millis,
                        MAX_DELAY.toMillis()
                )
        );
    }
}
