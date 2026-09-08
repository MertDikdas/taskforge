package com.taskforge.api.job.service;

import com.taskforge.api.job.dto.CreateJobRequest;
import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.messaging.JobPublisher;
import com.taskforge.api.messaging.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JobSubmissionService {
    private final JobService jobService;
    private final OutboxService outboxService;

    @Transactional
    public JobResponse submit(CreateJobRequest request) {
        JobResponse job = jobService.create(request);
        outboxService.enqueueJobExecution(job.id());
        return job;
    }
}
