package com.taskforge.api.job.repository;

import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findAllByStatus(
            JobStatus status,
            Pageable pageable
    );
}
