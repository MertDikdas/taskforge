package com.taskforge.api.job.service;

import com.taskforge.api.common.exception.JobNotFoundException;
import com.taskforge.domain.job.Job;
import com.taskforge.domain.job.JobPriority;
import com.taskforge.api.job.dto.CreateJobRequest;
import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.job.dto.PageResponse;
import com.taskforge.api.job.repository.JobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class JobService {
    private static final JobPriority DEFAULT_PRIORITY = JobPriority.NORMAL;
    private static final int DEFAULT_MAX_RETRIES = 3;

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public JobResponse create(CreateJobRequest request){
        JobPriority priority = request.priority() == null
                ? DEFAULT_PRIORITY
                : request.priority();
        int maxRetries = request.maxRetries() == null
                ? DEFAULT_MAX_RETRIES
                : request.maxRetries();

        Job job = Job.queued(
                request.type(),
                priority,
                request.payload(),
                maxRetries
        );

        Job savedJob = jobRepository.save(job);
        return toResponse(savedJob);
    }

    @Transactional
    public JobResponse getById(UUID id){
        Job job = jobRepository.findById(id)
                .orElseThrow(
                        ()->new JobNotFoundException(id)
                );
        return toResponse(job);
    }
    @Transactional
    public PageResponse<JobResponse> getAll(int page, int size){
        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        Page<JobResponse> result = jobRepository
                .findAll(pageable)
                .map(this::toResponse);

        return PageResponse.from(result);

    }

    private JobResponse toResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getType(),
                job.getStatus(),
                job.getPriority(),
                job.getPayload(),
                job.getRetryCount(),
                job.getMaxRetries(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}
