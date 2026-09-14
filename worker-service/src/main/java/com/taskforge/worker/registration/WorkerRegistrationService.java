package com.taskforge.worker.registration;

import com.taskforge.worker.WorkerIdentity;
import com.taskforge.worker.node.WorkerNode;
import com.taskforge.worker.node.WorkerNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class WorkerRegistrationService {

    private final WorkerNodeRepository workerNodeRepository;
    private final WorkerIdentity workerIdentity;

    @Transactional
    public void register() {
        Instant now = Instant.now();
        String workerId = workerIdentity.getId();

        WorkerNode workerNode = workerNodeRepository
                .findById(workerId)
                .orElseGet(() -> new WorkerNode(workerId, now));

        workerNode.register(now);

        workerNodeRepository.save(workerNode);
    }
}
