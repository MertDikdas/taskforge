package com.taskforge.worker.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WorkerRegistrationRunner implements ApplicationRunner {
    private final WorkerRegistrationService workerRegistrationService;

    @Override
    public void run(ApplicationArguments args) {

        workerRegistrationService.register();
    }
}
