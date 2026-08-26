package com.taskforge.worker.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import com.taskforge.worker.job.execution.JobExecution;
import com.taskforge.worker.job.handler.JobHandler;
import com.taskforge.worker.job.handler.JobHandlerRegistry;
import com.taskforge.worker.job.service.JobExecutionStateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobMessageListener {

    private final JobExecutionStateService stateService;
    private final JobHandlerRegistry handlerRegistry;

    @RabbitListener(
            queues = JobMessagingContract.WORKER_QUEUE
    )
    public void consume(String jobIdValue) {

        UUID jobId = UUID.fromString(jobIdValue);

        JobExecution job = stateService.start(jobId);

        try {
            JobHandler handler =
                    handlerRegistry.get(job.type());

            handler.execute(job);

            stateService.complete(jobId);

            log.info(
                    "Job {} completed successfully",
                    jobId
            );

        } catch (Exception exception) {

            stateService.fail(jobId);

            log.error(
                    "Job {} failed during execution",
                    jobId,
                    exception
            );
        }
    }
}