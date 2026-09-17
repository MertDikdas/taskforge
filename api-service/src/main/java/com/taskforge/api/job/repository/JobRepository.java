package com.taskforge.api.job.repository;

import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {
    Page<Job> findAllByStatus(
            JobStatus status,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select j
    from Job j
    where j.id = :id
    """)
    Optional<Job> findByIdForUpdate(@Param("id") UUID id);
}
