ALTER TABLE jobs
DROP CONSTRAINT chk_jobs_status;

ALTER TABLE jobs
    ADD CONSTRAINT chk_jobs_status
        CHECK (
            status IN (
                       'QUEUED',
                       'RUNNING',
                       'RETRYING',
                       'COMPLETED',
                       'FAILED',
                       'DEAD_LETTER',
                       'CANCELLED',
                       'CANCEL_REQUESTED'
                )
            );