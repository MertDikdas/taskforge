package com.taskforge.worker;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Slf4j
public class WorkerIdentity {

    private final String id;

    public WorkerIdentity(
            @Value("${taskforge.worker.id}") String id
    ) {
        this.id = id;
    }

    @PostConstruct
    public void logIdentity() {
        log.info("Worker started with id={}", id);
    }
}