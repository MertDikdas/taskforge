package com.taskforge.contracts.messaging;

public final class JobMessagingContract {
    public JobMessagingContract() {
    }

    public static final String EXCHANGE =
            "taskforge.jobs";

    public static final String WORKER_QUEUE =
            "taskforge.worker.jobs";

    public static final String EXECUTE_ROUTING_KEY =
            "job.execute";

}
