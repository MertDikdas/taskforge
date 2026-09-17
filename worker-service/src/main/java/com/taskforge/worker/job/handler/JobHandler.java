package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import com.taskforge.worker.job.execution.JobExecution;
import com.taskforge.worker.job.execution.JobExecutionContext;

public interface JobHandler {
    JobType supportedType();

    void execute(JobExecutionContext jobExecutionContext);
}
