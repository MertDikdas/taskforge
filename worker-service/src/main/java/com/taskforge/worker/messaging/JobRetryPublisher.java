package com.taskforge.worker.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JobRetryPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(
            UUID jobId,
            int retryNumber
    ) {

        String routingKey =
                routingKeyFor(retryNumber);

        rabbitTemplate.convertAndSend(
                JobMessagingContract.EXCHANGE,
                routingKey,
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

    private String routingKeyFor(int retryNumber) {

        return switch (retryNumber) {
            case 1 ->
                    JobMessagingContract.RETRY_1_ROUTING_KEY;

            case 2 ->
                    JobMessagingContract.RETRY_2_ROUTING_KEY;

            default ->
                    JobMessagingContract.RETRY_3_ROUTING_KEY;
        };
    }
}