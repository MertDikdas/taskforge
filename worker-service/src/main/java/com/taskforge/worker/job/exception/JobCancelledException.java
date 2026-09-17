package com.taskforge.worker.job.exception;

import java.util.UUID;

public class JobCancelledException extends RuntimeException {
    public JobCancelledException(UUID jobId) {
        super("Job cancelled: " + jobId);
    }
}
