package com.taskforge.worker.job.repository;

import com.taskforge.domain.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
}
