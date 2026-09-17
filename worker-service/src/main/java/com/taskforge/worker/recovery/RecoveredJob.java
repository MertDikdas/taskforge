package com.taskforge.worker.recovery;

import java.util.UUID;

public record RecoveredJob(
        UUID jobId,
        int retryNumber
) {
}