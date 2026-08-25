CREATE TABLE jobs (
                      id UUID PRIMARY KEY,

                      type VARCHAR(32) NOT NULL,
                      status VARCHAR(32) NOT NULL,
                      priority VARCHAR(16) NOT NULL,

                      payload JSONB NOT NULL,

                      retry_count INTEGER NOT NULL DEFAULT 0,
                      max_retries INTEGER NOT NULL DEFAULT 3,

                      created_at TIMESTAMPTZ NOT NULL,
                      updated_at TIMESTAMPTZ NOT NULL,

                      CONSTRAINT chk_jobs_retry_count
                          CHECK (retry_count >= 0),

                      CONSTRAINT chk_jobs_max_retries
                          CHECK (max_retries >= 0 AND max_retries <= 20),

                      CONSTRAINT chk_jobs_type
                          CHECK (type IN (
                                          'PDF_REPORT',
                                          'CSV_IMPORT',
                                          'IMAGE_RESIZE',
                                          'EMAIL_BATCH'
                              )),

                      CONSTRAINT chk_jobs_priority
                          CHECK (priority IN (
                                              'LOW',
                                              'NORMAL',
                                              'HIGH',
                                              'CRITICAL'
                              )),

                      CONSTRAINT chk_jobs_status
                          CHECK (status IN (
                                            'QUEUED',
                                            'RUNNING',
                                            'COMPLETED',
                                            'FAILED',
                                            'RETRYING',
                                            'DEAD_LETTER',
                                            'CANCELLED',
                                            'SCHEDULED'
                              ))
);

CREATE INDEX idx_jobs_created_at
    ON jobs (created_at DESC);