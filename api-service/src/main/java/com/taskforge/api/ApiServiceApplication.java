package com.taskforge.api;

import com.taskforge.api.messaging.outbox.OutboxMessage;
import com.taskforge.domain.job.Job;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
@EntityScan(basePackageClasses = {
        Job.class,
        OutboxMessage.class
})
public class ApiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiServiceApplication.class, args);
	}

}
