package com.taskforge.api.job.dto;

import com.taskforge.domain.job.JobPriority;
import com.taskforge.domain.job.JobStatus;
import com.taskforge.domain.job.JobType;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record JobResponse(
        UUID id,
        JobType type,
        JobStatus status,
        JobPriority priority,
        Map<String, Object> payload,
        int retryCount,
        int maxRetries,
        Instant createdAt,
        Instant updatedAt,
        String lastError,
        Instant nextRetryAt
) {
}