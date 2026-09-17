package com.taskforge.worker.cancellation;

import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobStatus;
import com.taskforge.worker.WorkerIdentity;
import com.taskforge.worker.job.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobCancellationListener {

    private final JobRepository jobRepository;
    private final WorkerIdentity workerIdentity;
    private final JobCancellationRegistry jobCancellationRegistry;

    @RabbitListener(
            queues = "#{workerCancelQueue.name}"
    )
    @Transactional
    public void handleCancellation(String jobIdValue){

        UUID jobId = UUID.fromString(jobIdValue);

        Job job = jobRepository.findById(jobId).orElse(null);

        if(job == null){
            log.warn("Job with id {} not found", jobIdValue);
            return;
        }

        if(job.getStatus() != JobStatus.CANCEL_REQUESTED){
            log.info(
                    "Ignoring stale cancellation for job {} with status {}",
                    jobId,
                    job.getStatus()
            );
            return;
        }
        if (!workerIdentity.getId().equals(job.getWorkerId())) {
            log.info(
                    "Ignoring cancellation for job {} owned by worker {}",
                    jobId,
                    job.getWorkerId()
            );
            return;
        }
        jobCancellationRegistry.request(jobId);

        log.info(
                "Worker {} received cancellation request for job {}",
                workerIdentity.getId(),
                jobId
        );

    }
}
