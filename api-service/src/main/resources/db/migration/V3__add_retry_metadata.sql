ALTER TABLE jobs
    ADD COLUMN last_error TEXT,
    ADD COLUMN next_retry_at TIMESTAMPTZ;