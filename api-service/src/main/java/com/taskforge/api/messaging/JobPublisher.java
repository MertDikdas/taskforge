package com.taskforge.api.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JobPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(UUID jobId){
        rabbitTemplate.convertAndSend(
                JobMessagingContract.EXCHANGE,
                JobMessagingContract.EXECUTE_ROUTING_KEY,
                jobId.toString(),
                message -> {
                    message.getMessageProperties()
                            .setDeliveryMode(
                                    MessageDeliveryMode.PERSISTENT
                            );
                    return message;
                }

        );
    }
}
