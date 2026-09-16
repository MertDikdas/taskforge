package com.taskforge.worker.job.repository;

import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobStatus;
import jakarta.persistence.LockModeType;
import org.hibernate.LockMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
            """
            SELECT j
            FROM Job j
            WHERE j.id=:id
            """)
    Optional<Job> findByIdForUpdate(@Param("id") UUID id);

    List<Job> findByWorkerIdAndStatus(
            String workerId,
            JobStatus status
    );
}
