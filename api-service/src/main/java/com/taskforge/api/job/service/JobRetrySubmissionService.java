package com.taskforge.api.job.service;

import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.messaging.JobPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobRetrySubmissionService {
    private final JobService jobService;
    private final JobPublisher  jobPublisher;

    public JobResponse retry(UUID jobId){
        JobResponse job =
                jobService.resetForManuelRetry(jobId);

        jobPublisher.publish(job.id());
        return job;

    }
}
