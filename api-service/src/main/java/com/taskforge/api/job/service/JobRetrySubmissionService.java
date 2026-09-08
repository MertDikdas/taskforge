package com.taskforge.api.job.service;

import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.messaging.JobPublisher;
import com.taskforge.api.messaging.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobRetrySubmissionService {
    private final JobService jobService;
    private final OutboxService outboxService;

    @Transactional
    public JobResponse retry(UUID jobId){
        JobResponse job =
                jobService.resetForManuelRetry(jobId);
        outboxService.enqueueJobExecution(job.id());
        return job;

    }
}
