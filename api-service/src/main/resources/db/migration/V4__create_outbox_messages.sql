CREATE TABLE outbox_messages
(
    id            UUID PRIMARY KEY,
    aggregate_id  UUID         NOT NULL,
    routing_key   VARCHAR(100) NOT NULL,
    payload       TEXT         NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL,
    published_at  TIMESTAMPTZ,
    attempt_count INTEGER      NOT NULL DEFAULT 0,
    last_error    TEXT
);

CREATE INDEX idx_outbox_messages_unpublished
    ON outbox_messages (created_at)
    WHERE published_at IS NULL;