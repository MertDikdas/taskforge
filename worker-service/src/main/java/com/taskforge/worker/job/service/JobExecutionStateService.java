package com.taskforge.worker.job.service;

import com.taskforge.domain.job.Job;
import com.taskforge.worker.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobExecutionStateService {
    private final JobRepository jobRepository;

    @Transactional
    public JobExecution start(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalStateException("Job not found: " + jobId)
                );
        job.markRunning(Instant.now());

        return new JobExecution(
                job.getId(),
                job.getType(),
                job.getPayload()
        );
    }

    @Transactional
    public void complete(UUID jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalStateException("Job not found: " + jobId)
                );

        job.markCompleted(Instant.now());
    }
}
