package com.taskforge.api.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {

    @Bean
    public DirectExchange jobExchange() {
        return new DirectExchange(
                JobMessagingContract.EXCHANGE,
                true,
                false
        );
    }

    @Bean
    public Queue jobsQueue() {
        return QueueBuilder
                .durable(JobMessagingContract.WORKER_QUEUE)
                .withArgument(
                        "x-dead-letter-exchange",
                        JobMessagingContract.EXCHANGE
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        JobMessagingContract.DEAD_LETTER_ROUTING_KEY
                )
                .build();
    }

    @Bean
    public Binding jobsBinding(
            Queue jobsQueue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder
                .bind(jobsQueue)
                .to(jobsExchange)
                .with(JobMessagingContract.EXECUTE_ROUTING_KEY);
    }
}
