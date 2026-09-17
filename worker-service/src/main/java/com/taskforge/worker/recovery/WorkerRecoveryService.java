package com.taskforge.worker.recovery;

import com.taskforge.worker.messaging.JobRetryPublisher;
import com.taskforge.worker.node.WorkerNode;
import com.taskforge.worker.node.WorkerNodeRepository;
import com.taskforge.worker.node.WorkerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkerRecoveryService {

    private final WorkerNodeRepository workerNodeRepository;
    private final WorkerRecoveryProcessor workerRecoveryProcessor;
    private final JobRetryPublisher jobRetryPublisher;

    @Scheduled(fixedDelay = 5000)
    public void recover() {

        Instant cutoff = Instant.now().minusSeconds(10);

        List<WorkerNode> staleWorkers =
                workerNodeRepository.findByStatusAndLastHeartbeatAtBefore(
                        WorkerStatus.ONLINE,
                        cutoff
                );

        for (WorkerNode worker : staleWorkers) {
            List<RecoveredJob> recoveredJobs =
                    workerRecoveryProcessor.recover(
                            worker.getId(),
                            cutoff
                    );

            for (RecoveredJob recoveredJob : recoveredJobs) {
                jobRetryPublisher.publish(
                        recoveredJob.jobId(),
                        recoveredJob.retryNumber()
                );
            }
        }
    }
}