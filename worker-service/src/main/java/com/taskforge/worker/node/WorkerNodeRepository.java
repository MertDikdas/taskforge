package com.taskforge.worker.node;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface WorkerNodeRepository extends JpaRepository<WorkerNode, String> {
    List<WorkerNode> findByStatusAndLastHeartbeatAtBefore(
            WorkerStatus status,
            Instant lastHeartbeatAt
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select w
    from WorkerNode w
    where w.id = :id
    """)
    Optional<WorkerNode> findByIdForUpdate(@Param("id") String id);
}
