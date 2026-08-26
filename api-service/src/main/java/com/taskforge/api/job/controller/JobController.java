package com.taskforge.api.job.controller;

import com.taskforge.api.job.dto.CreateJobRequest;
import com.taskforge.api.job.dto.JobResponse;
import com.taskforge.api.job.dto.PageResponse;
import com.taskforge.api.job.service.JobService;
import com.taskforge.api.job.service.JobSubmissionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@Validated
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    private final JobSubmissionService jobSubmissionService;


    @PostMapping
    public ResponseEntity<JobResponse> create(
            @Valid @RequestBody CreateJobRequest request
    ){
        JobResponse response = jobSubmissionService.submit(request);

        return ResponseEntity
                .accepted()
                .location(URI.create("/api/jobs/"+response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public JobResponse getById(@PathVariable UUID id) {
        return jobService.getById(id);
    }

    @GetMapping
    public PageResponse<JobResponse> getAll(
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size
    ){
        return jobService.getAll(page, size);
    }



}
