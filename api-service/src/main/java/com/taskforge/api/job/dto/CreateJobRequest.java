package com.taskforge.api.job.dto;

import com.taskforge.api.job.domain.JobPriority;
import com.taskforge.api.job.domain.JobType;
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
