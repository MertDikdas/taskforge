package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import com.taskforge.worker.job.service.JobExecution;

public interface JobHandler {
    JobType supportedType();

    void execute(JobExecution job);
}
