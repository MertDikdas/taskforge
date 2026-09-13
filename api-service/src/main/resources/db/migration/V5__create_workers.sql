CREATE TABLE workers (
                         id VARCHAR(100) PRIMARY KEY,
                         status VARCHAR(20) NOT NULL,
                         started_at TIMESTAMPTZ NOT NULL,
                         last_heartbeat_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_workers_last_heartbeat_at
    ON workers(last_heartbeat_at);