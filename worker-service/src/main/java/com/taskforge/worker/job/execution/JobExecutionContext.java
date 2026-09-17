package com.taskforge.worker.job.execution;

import com.taskforge.worker.cancellation.JobCancellationRegistry;
import com.taskforge.worker.job.exception.JobCancelledException;

public class JobExecutionContext {

    private final JobExecution jobExecution;
    private final JobCancellationRegistry cancellationRegistry;

    public JobExecutionContext(
            JobExecution jobExecution,
            JobCancellationRegistry cancellationRegistry
    ) {
        this.jobExecution = jobExecution;
        this.cancellationRegistry = cancellationRegistry;
    }

    public JobExecution getJobExecution() {
        return jobExecution;
    }

    public boolean isCancellationRequested() {
        return cancellationRegistry
                .isCancellationRequested(jobExecution.id());
    }

    public void checkCancellation() {
        if (isCancellationRequested()) {
            throw new JobCancelledException(
                    jobExecution.id()
            );
        }
    }
}