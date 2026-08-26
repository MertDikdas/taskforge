package com.taskforge.worker.job.service;

import com.taskforge.domain.job.Job;
import com.taskforge.worker.job.execution.JobExecution;
import com.taskforge.worker.job.repository.JobRepository;
import com.taskforge.worker.job.retry.RetrySchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
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
                job.getPayload(),
                job.getRetryCount(),
                job.getMaxRetries()
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

    @Transactional
    public void fail(UUID jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Job not found: " + jobId
                        )
                );

        job.markFailed(Instant.now());
    }

    @Transactional
    public RetrySchedule prepareRetry(
            UUID jobId,
            String error,
            Duration delay
    ) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Job not found: " + jobId
                        )
                );

        if (job.getRetryCount()
                >= job.getMaxRetries()) {
            return null;
        }

        Instant now = Instant.now();

        Instant nextRetryAt =
                now.plus(delay);

        job.markRetrying(
                now,
                truncateError(error),
                nextRetryAt
        );

        return new RetrySchedule(
                job.getRetryCount(),
                nextRetryAt
        );
    }

    private String truncateError(String error) {

        if (error == null) {
            return null;
        }

        int maxLength = 2_000;

        return error.length() <= maxLength
                ? error
                : error.substring(0, maxLength);
    }


    @Transactional
    public void deadLetter(
            UUID jobId,
            String error
    ) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Job not found: " + jobId
                        )
                );

        job.markDeadLetter(
                Instant.now(),
                error
        );
    }
}
