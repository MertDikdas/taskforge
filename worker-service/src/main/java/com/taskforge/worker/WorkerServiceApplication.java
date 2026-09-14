package com.taskforge.worker;

import com.taskforge.domain.job.Job;
import com.taskforge.worker.node.WorkerNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackageClasses =
        {
                Job.class,
                WorkerNode.class
        }
)
@EnableScheduling
public class WorkerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkerServiceApplication.class, args);
	}

}
