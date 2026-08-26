package com.taskforge.contracts.messaging;

public final class JobMessagingContract {

    private JobMessagingContract() {
    }

    public static final String EXCHANGE =
            "taskforge.jobs";

    public static final String WORKER_QUEUE =
            "taskforge.worker.jobs";


    public static final String DEAD_LETTER_QUEUE =
            "taskforge.worker.jobs.dead-letter";

    public static final String EXECUTE_ROUTING_KEY =
            "job.execute";

    public static final String DEAD_LETTER_ROUTING_KEY =
            "job.dead-letter";

    public static final String RETRY_1_QUEUE =
            "taskforge.worker.jobs.retry.1";

    public static final String RETRY_2_QUEUE =
            "taskforge.worker.jobs.retry.2";

    public static final String RETRY_3_QUEUE =
            "taskforge.worker.jobs.retry.3";

    public static final String RETRY_1_ROUTING_KEY =
            "job.retry.1";

    public static final String RETRY_2_ROUTING_KEY =
            "job.retry.2";

    public static final String RETRY_3_ROUTING_KEY =
            "job.retry.3";
}