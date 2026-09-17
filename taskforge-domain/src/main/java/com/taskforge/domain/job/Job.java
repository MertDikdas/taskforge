package com.taskforge.domain.job;

import com.taskforge.domain.job.exception.InvalidJobStateException;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "jobs")
@Getter
public class Job {
    @Id
    private UUID id;

    @Column(name = "worker_id")
    private String workerId;

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

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

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
    public void resetForManualRetry(Instant now) {

        if (status != JobStatus.DEAD_LETTER
                && status != JobStatus.FAILED) {

            throw new InvalidJobStateException(
                    "Job cannot be manually retried from state: " + status
            );
        }

        status = JobStatus.QUEUED;
        retryCount = 0;
        nextRetryAt = null;
        completedAt = null;
        workerId = null;
        updatedAt = now;
    }

    public boolean tryMarkRunning(Instant now, String workerId) {

        if (status != JobStatus.QUEUED
                && status != JobStatus.RETRYING) {
            return false;
        }

        status = JobStatus.RUNNING;

        if (startedAt == null) {
            startedAt = now;
        }

        nextRetryAt = null;
        updatedAt = now;
        this.workerId = workerId;

        return true;
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
        workerId = null;
    }

    public void markFailed(Instant now) {

        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                    "Job can only fail from RUNNING state. Current state: "
                            + status
            );
        }

        status = JobStatus.FAILED;
        workerId = null;
        updatedAt = now;
    }

    public void markRetrying(
            Instant now,
            String error,
            Instant nextRetryAt
    ) {

        if (status != JobStatus.RUNNING) {
            throw new IllegalStateException(
                    "Job can only retry from RUNNING state. Current state: "
                            + status
            );
        }

        retryCount++;
        status = JobStatus.RETRYING;
        lastError = error;
        this.nextRetryAt = nextRetryAt;
        workerId = null;
        updatedAt = now;
    }


    public void markDeadLetter(
            Instant now,
            String error
    ) {

        if (status != JobStatus.RUNNING
                && status != JobStatus.RETRYING) {
            throw new IllegalStateException(
                    "Job cannot move to DEAD_LETTER from state: "
                            + status
            );
        }

        status = JobStatus.DEAD_LETTER;
        lastError = error;
        nextRetryAt = null;
        workerId = null;
        updatedAt = now;
    }

    public void cancel(Instant now){
        if (status == JobStatus.QUEUED || status == JobStatus.RETRYING){

            status = JobStatus.CANCELLED;
            workerId = null;
            updatedAt = now;
            lastError = null;
            nextRetryAt = null;
            return;
        }

        if(status== JobStatus.RUNNING){
            status = JobStatus.CANCEL_REQUESTED;
            updatedAt = now;
            return;
        }


        throw new InvalidJobStateException(
                "Job cannot be cancelled from state: " + status
        );
    }
    public void markCancelled(Instant now) {
        if (status != JobStatus.CANCEL_REQUESTED) {
            throw new IllegalStateException(
                    "Job cannot be marked cancelled from state: " + status
            );
        }

        status = JobStatus.CANCELLED;
        workerId = null;
        nextRetryAt = null;
        lastError = null;
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
}
