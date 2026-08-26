package com.taskforge.domain.job;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "jobs")
public class Job {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private JobType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private JobPriority priority;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private int maxRetries;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected Job(){
    }

    private Job(
            UUID id,
            JobType type,
            JobPriority priority,
            Map<String, Object> payload,
            int maxRetries,
            Instant createdAt
    ){
        this.id = id;
        this.type = type;
        this.status = JobStatus.QUEUED;
        this.priority = priority;
        this.payload = new HashMap<>(payload);
        this.retryCount = 0;
        this.maxRetries = maxRetries;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void markRunning(Instant now){
        if(status!=JobStatus.QUEUED){
            throw new IllegalStateException("Job can only start from QUEUED state. Current state: "+ status);
        }
        status = JobStatus.RUNNING;
        startedAt = now;
        updatedAt = now;
    }

    public void markCompleted(Instant now) {

        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                    "Job can only complete from RUNNING state. Current state: " + status
            );
        }

        status = JobStatus.COMPLETED;
        completedAt = now;
        updatedAt = now;
    }

    public static Job queued(
            JobType type,
            JobPriority priority,
            Map<String, Object> payload,
            int maxRetries
    ){
        return new Job(UUID.randomUUID(), type, priority, payload, maxRetries, Instant.now());
    }
    public UUID getId() {
        return id;
    }

    public JobType getType() {
        return type;
    }

    public JobStatus getStatus() {
        return status;
    }

    public JobPriority getPriority() {
        return priority;
    }

    public Map<String, Object> getPayload() {
        return Map.copyOf(payload);
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }
}
