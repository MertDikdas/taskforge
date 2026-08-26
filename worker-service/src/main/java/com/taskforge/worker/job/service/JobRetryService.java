package com.taskforge.worker.job.service;

import com.taskforge.worker.job.retry.RetryPolicy;
import com.taskforge.worker.job.retry.RetrySchedule;
import com.taskforge.worker.messaging.JobDeadLetterPublisher;
import com.taskforge.worker.messaging.JobRetryPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRetryService {

    private final JobExecutionStateService stateService;
    private final JobRetryPublisher retryPublisher;
    private final RetryPolicy retryPolicy;
    private final JobDeadLetterPublisher deadLetterPublisher;

    public void handleFailure(
            UUID jobId,
            int currentRetryCount,
            Exception exception
    ) {

        String error =
                exception.getMessage();

        if (!retryPolicy.isRetryable(exception)) {

            stateService.deadLetter(
                    jobId,
                    error
            );
            deadLetterPublisher.publish(jobId);


            log.error(
                    "Job {} failed with a non-retryable error",
                    jobId,
                    exception
            );

            return;
        }

        int nextRetryNumber =
                currentRetryCount + 1;

        Duration delay =
                retryPolicy.delayFor(
                        nextRetryNumber
                );

        RetrySchedule schedule =
                stateService.prepareRetry(
                        jobId,
                        error,
                        delay
                );

        if (schedule == null) {

            stateService.deadLetter(
                    jobId,
                    error
            );

            log.error(
                    "Job {} exhausted all retry attempts",
                    jobId,
                    exception
            );

            return;
        }

        retryPublisher.publish(
                jobId,
                schedule.retryNumber()
        );

        log.warn(
                "Job {} scheduled for retry {} at {}",
                jobId,
                schedule.retryNumber(),
                schedule.nextRetryAt()
        );
    }
}