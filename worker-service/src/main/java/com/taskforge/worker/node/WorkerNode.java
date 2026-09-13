package com.taskforge.worker.node;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "workers")
@Getter
@NoArgsConstructor
public class WorkerNode {

    @Id
    @Column(length = 100)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WorkerStatus status;

    @Column(name = "started_at", nullable = false)
    private Instant startedAt;

    @Column(name = "last_heartbeat_at", nullable = false)
    private Instant lastHeartbeatAt;

    public WorkerNode(String id, Instant now) {
        this.id = id;
        this.status = WorkerStatus.ONLINE;
        this.startedAt = now;
        this.lastHeartbeatAt = now;
    }

    public void register(Instant now) {
        this.status = WorkerStatus.ONLINE;
        this.startedAt = now;
        this.lastHeartbeatAt = now;
    }

    public void heartbeat(Instant now) {
        this.lastHeartbeatAt = now;
        this.status = WorkerStatus.ONLINE;
    }

    public void markOffline() {
        this.status = WorkerStatus.OFFLINE;
    }
}