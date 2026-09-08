package com.taskforge.api.messaging.outbox;

import com.taskforge.api.messaging.JobPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {
    private OutboxMessageRepository outboxMessageRepository;
    private final JobPublisher jobPublisher;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish(){
       List<OutboxMessage> messages = outboxMessageRepository.findTop50ByPublishedAtIsNullOrderByCreatedAtAsc();
       for(OutboxMessage outboxMessage : messages ){
           try {
               jobPublisher.publish(
                       outboxMessage.getRoutingKey(),
                       outboxMessage.getPayload()
               );
               outboxMessage.markPublished();
           } catch (Exception exception) {
               outboxMessage.markFailed(exception.getMessage());
           }
       }

    }
}
