package com.taskforge.worker.job.retry;

import java.time.Instant;

public record RetrySchedule(
        int retryNumber,
        Instant nextRetryAt
) {
}