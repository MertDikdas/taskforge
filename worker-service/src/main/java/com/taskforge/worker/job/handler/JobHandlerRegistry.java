package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class JobHandlerRegistry {

    private final Map<JobType, JobHandler> handlers;

    public JobHandlerRegistry(List<JobHandler> jobHandlers) {

        Map<JobType, JobHandler> registry =
                new EnumMap<>(JobType.class);

        for (JobHandler handler : jobHandlers) {

            JobHandler previous = registry.put(
                    handler.supportedType(),
                    handler
            );

            if (previous != null) {
                throw new IllegalStateException(
                        "Multiple handlers registered for "
                                + handler.supportedType()
                );
            }
        }

        this.handlers = Map.copyOf(registry);
    }

    public JobHandler get(JobType type) {

        JobHandler handler = handlers.get(type);

        if (handler == null) {
            throw new IllegalStateException(
                    "No handler registered for " + type
            );
        }

        return handler;
    }
}