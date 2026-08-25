package com.taskforge.api.job.dto;

import com.taskforge.api.job.domain.JobPriority;
import com.taskforge.api.job.domain.JobStatus;
import com.taskforge.api.job.domain.JobType;

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
        Instant updatedAt
) {
}