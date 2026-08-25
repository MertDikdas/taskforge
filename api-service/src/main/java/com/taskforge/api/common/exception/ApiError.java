package com.taskforge.api.common.exception;

import java.time.Instant;

public record ApiError(
        int status,
        String message,
        Instant timestamp
) {
}
