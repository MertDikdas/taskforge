package com.taskforge.worker;

import com.taskforge.domain.job.Job;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;

@SpringBootApplication
@EntityScan(basePackageClasses = Job.class)
public class WorkerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerServiceApplication.class, args);
	}

}
