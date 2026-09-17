package com.taskforge.worker.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import com.taskforge.worker.WorkerIdentity;
import com.taskforge.worker.cancellation.JobCancellationRegistry;
import com.taskforge.worker.job.exception.JobCancelledException;
import com.taskforge.worker.job.execution.JobExecution;
import com.taskforge.worker.job.execution.JobExecutionContext;
import com.taskforge.worker.job.handler.JobHandler;
import com.taskforge.worker.job.handler.JobHandlerRegistry;
import com.taskforge.worker.job.service.JobExecutionStateService;
import com.taskforge.worker.job.service.JobRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobMessageListener {

    private final WorkerIdentity workerIdentity;
    private final JobExecutionStateService stateService;
    private final JobHandlerRegistry handlerRegistry;
    private final JobRetryService retryService;
    private final JobCancellationRegistry cancellationRegistry;

    @RabbitListener(
            queues = JobMessagingContract.WORKER_QUEUE
    )
    public void consume(String jobIdValue) {

        UUID jobId = UUID.fromString(jobIdValue);

        Optional<JobExecution> execution =
                stateService.start(jobId);

        if (execution.isEmpty()) {
            log.info(
                    "Worker {} ignored duplicate/already claimed job {}",
                    workerIdentity.getId(),
                    jobId
            );
            return;
        }

        JobExecution job = execution.get();

        JobExecutionContext context =
                new JobExecutionContext(
                        job,
                        cancellationRegistry
                );

        log.info(
                "Worker {} claimed job {}",
                workerIdentity.getId(),
                jobId
        );

        try {

            context.checkCancellation();

            JobHandler handler =
                    handlerRegistry.get(job.type());

            handler.execute(context);

            context.checkCancellation();

            stateService.complete(jobId);

            log.info(
                    "Job {} completed successfully",
                    jobId
            );

        } catch (JobCancelledException exception) {

            stateService.markCancelled(jobId);

            log.info(
                    "Job {} cancelled by worker {}",
                    jobId,
                    workerIdentity.getId()
            );

        } catch (Exception exception) {

            retryService.handleFailure(
                    jobId,
                    job.retryCount(),
                    exception
            );

        } finally {

            cancellationRegistry.clear(jobId);
        }
    }
}