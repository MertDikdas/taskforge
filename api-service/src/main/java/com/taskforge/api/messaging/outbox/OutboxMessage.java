package com.taskforge.api.messaging.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_messages")
@Getter
@NoArgsConstructor
public class OutboxMessage {
    @Id
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "routing_key", nullable = false)
    private String routingKey;

    @Column(name= "payload", nullable = false)
    private String payload;

    @Column(name="created_at", nullable = false)
    private Instant createdAt;

    @Column(name= "published_at")
    private Instant publishedAt;

    @Column(name = "attempt_count", nullable = false)
    private int attemptCount;

    @Column(name = "last_error")
    private String lastError;

    public void markPublished(){
        this.publishedAt = Instant.now();
        this.lastError = null;
    }

    public void markFailed(String errorMessage){
        this.attemptCount++;
        this.lastError = errorMessage;
    }

    public OutboxMessage(UUID aggregateId, String routingKey, String payload) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.routingKey = routingKey;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.attemptCount = 0;
    }
}
