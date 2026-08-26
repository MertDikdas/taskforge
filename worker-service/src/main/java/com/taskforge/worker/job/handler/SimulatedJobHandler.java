package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import com.taskforge.worker.job.execution.JobExecution;

public class SimulatedJobHandler implements JobHandler {
    private final JobType type;

    public SimulatedJobHandler(JobType type) {
        this.type = type;
    }

    @Override
    public JobType supportedType() {
        return type;
    }

    @Override
    public void execute(JobExecution job) {

        System.out.printf(
                "Processing job %s of type %s%n",
                job.id(),
                job.type()
        );

        try {
            Thread.sleep(5000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Job execution interrupted",
                    exception
            );
        }

        System.out.printf(
                "Finished job %s%n",
                job.id()
        );
    }
}
