package com.taskforge.worker.node;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WorkerNodeRepository extends JpaRepository<WorkerNode, String> {
    List<WorkerNode> findByStatusAndLastHeartbeatAtBefore(
            WorkerStatus status,
            Instant lastHeartbeatAt
    );

}
