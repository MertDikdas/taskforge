package com.taskforge.api.messaging.outbox;

import com.taskforge.contracts.messaging.JobMessagingContract;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {
    private final OutboxMessageRepository outboxMessageRepository;

    public void enqueueJobExecution(UUID jobId){
        OutboxMessage outboxMessage = new OutboxMessage(
                jobId,
                JobMessagingContract.EXECUTE_ROUTING_KEY,
                jobId.toString()
        );

        outboxMessageRepository.save(outboxMessage);
    }

    public void enqueueJobCancellation(UUID jobId, String workerId){
        OutboxMessage outboxMessage = new OutboxMessage(
                jobId,
                JobMessagingContract.cancelRoutingKey(workerId),
                jobId.toString()
        );
        outboxMessageRepository.save(outboxMessage);
    }
}
