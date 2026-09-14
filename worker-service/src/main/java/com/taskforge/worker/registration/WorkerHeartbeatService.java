package com.taskforge.worker.registration;

import com.taskforge.worker.WorkerIdentity;
import com.taskforge.worker.node.WorkerNode;
import com.taskforge.worker.node.WorkerNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkerHeartbeatService {

    private final WorkerNodeRepository workerNodeRepository;
    private final WorkerIdentity workerIdentity;

    @Scheduled(
            fixedDelay = 5000
    )
    @Transactional
    public void heartbeat() {
        String workerId = workerIdentity.getId();
        WorkerNode workerNode = workerNodeRepository.findById(workerId).orElseThrow(() ->
                new IllegalStateException("Registered worker not found: " + workerId)
        );
        workerNode.heartbeat(Instant.now());
    }
}
