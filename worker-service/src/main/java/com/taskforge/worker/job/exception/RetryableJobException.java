package com.taskforge.worker.job.exception;

public class RetryableJobException extends RuntimeException {

    public RetryableJobException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

    public RetryableJobException(String message) {
        super(message);
    }
}