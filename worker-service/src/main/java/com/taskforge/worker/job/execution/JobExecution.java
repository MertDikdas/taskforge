package com.taskforge.worker.job.execution;

import com.taskforge.domain.job.JobType;

import java.util.Map;
import java.util.UUID;

public record JobExecution(
        UUID id,
        JobType type,
        Map<String, Object> payload,
        int retryCount,
        int maxRetries
) {
}