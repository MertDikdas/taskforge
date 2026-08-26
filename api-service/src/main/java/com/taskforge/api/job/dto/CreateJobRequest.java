package com.taskforge.api.job.dto;

import com.taskforge.domain.job.JobPriority;
import com.taskforge.domain.job.JobType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record CreateJobRequest(
        @NotNull
        JobType type,

        JobPriority priority,

        @NotNull
        Map<String, Object> payload,

        @Min(0)
        @Max(20)
        Integer maxRetries
) {
}
