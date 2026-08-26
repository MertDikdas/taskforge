package com.taskforge.api.job.service;

import com.taskforge.api.job.dto.CreateJobRequest;
import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.messaging.JobPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobSubmissionService {
    private final JobService jobService;
    private final JobPublisher jobPublisher;

    public JobResponse submit(CreateJobRequest request) {
        JobResponse job = jobService.create(request);
        jobPublisher.publish(job.id());
        return job;
    }
}
