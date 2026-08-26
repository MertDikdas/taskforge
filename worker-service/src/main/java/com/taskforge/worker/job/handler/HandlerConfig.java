package com.taskforge.worker.job.handler;

import com.taskforge.domain.job.JobType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HandlerConfig {

    @Bean
    public JobHandler imageResizeJobHandler() {
        return new SimulatedJobHandler(
                JobType.IMAGE_RESIZE
        );
    }

    @Bean
    public JobHandler emailBatchJobHandler() {
        return new SimulatedJobHandler(
                JobType.EMAIL_BATCH
        );
    }


}
