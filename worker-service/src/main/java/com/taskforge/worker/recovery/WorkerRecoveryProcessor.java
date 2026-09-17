package com.taskforge.worker.recovery;

import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobStatus;
import com.taskforge.worker.job.repository.JobRepository;
import com.taskforge.worker.job.retry.RetryPolicy;
import com.taskforge.worker.node.WorkerNode;
import com.taskforge.worker.node.WorkerNodeRepository;
import com.taskforge.worker.node.WorkerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerRecoveryProcessor {

    private final WorkerNodeRepository workerNodeRepository;
    private final JobRepository jobRepository;
    private final RetryPolicy  retryPolicy;

    /*
    * This method is responsible for recovering a worker node and its associated jobs. It performs the following steps:
    * 1. It locks the worker node for update to prevent concurrent modifications.
    * 2. It checks if the worker node is still online and if its last heartbeat is before the specified cutoff time. If not, it returns early.
    * 3. It marks the worker node as offline.
    * 4. It retrieves all jobs associated with the worker node that are currently running.
    * 5. For each job, it locks the job for update and checks if it is still running and associated with the same worker node. If not, it continues to the next job.
    * 6. If the job has reached its maximum retry count, it marks the job as dead letter. Otherwise, it marks the job as retrying and schedules the next retry.
    * */
    @Transactional
    public List<RecoveredJob> recover(String workerId, Instant cutoff) {
        List<RecoveredJob> recoveredJobs = new ArrayList<>();

        WorkerNode lockedWorker = workerNodeRepository
                .findByIdForUpdate(workerId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Worker node not found: " + workerId
                        )
                );

        if (lockedWorker.getStatus() != WorkerStatus.ONLINE) {
            return recoveredJobs;
        }

        if (!lockedWorker.getLastHeartbeatAt().isBefore(cutoff)) {
            return recoveredJobs;
        }

        lockedWorker.markOffline();

        List<Job> jobs =
                jobRepository.findByWorkerIdAndStatusIn(
                        workerId,
                        List.of(
                                JobStatus.RUNNING,
                                JobStatus.CANCEL_REQUESTED
                        )
                );

        for (Job job : jobs) {

            Job lockedJob = jobRepository
                    .findByIdForUpdate(job.getId())
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Job not found: " + job.getId()
                            )
                    );



            if (!workerId.equals(lockedJob.getWorkerId())) {
                continue;
            }

            Instant now = Instant.now();

            if (lockedJob.getStatus() == JobStatus.CANCEL_REQUESTED) {

                lockedJob.markCancelled(now);

                continue;
            }

            if (lockedJob.getStatus() != JobStatus.RUNNING) {
                continue;
            }
            int nextRetryNumber = lockedJob.getRetryCount() + 1;

            if (lockedJob.getRetryCount() >= lockedJob.getMaxRetries()) {

                lockedJob.markDeadLetter(
                        now,
                        "Worker " + workerId + " crashed"
                );

            } else {

                Duration delay = retryPolicy.delayFor(nextRetryNumber);

                lockedJob.markRetrying(
                        now,
                        "Worker " + workerId + " crashed",
                        now.plus(delay)
                );

                recoveredJobs.add(
                        new RecoveredJob(
                                lockedJob.getId(),
                                nextRetryNumber
                        )
                );
            }

        }
        return recoveredJobs;
    }
}