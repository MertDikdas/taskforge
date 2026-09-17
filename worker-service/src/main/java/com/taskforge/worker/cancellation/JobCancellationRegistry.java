package com.taskforge.worker.cancellation;

import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobCancellationRegistry {

    private final Set<UUID> requested =
            ConcurrentHashMap.newKeySet();

    public void request(UUID jobId) {
        requested.add(jobId);
    }

    public boolean isCancellationRequested(UUID jobId) {
        return requested.contains(jobId);
    }

    public void clear(UUID jobId) {
        requested.remove(jobId);
    }
}