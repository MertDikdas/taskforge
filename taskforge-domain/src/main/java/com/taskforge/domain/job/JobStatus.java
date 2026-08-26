package com.taskforge.domain.job;

public enum JobStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    RETRYING,
    DEAD_LETTER,
    CANCELLED,
    SCHEDULED
}