package com.taskforge.worker.messaging;

import com.taskforge.contracts.messaging.JobMessagingContract;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitTopologyConfig {

    private static final int RETRY_DELAY_MS = 5_000;

    @Bean
    public DirectExchange jobsExchange() {
        return new DirectExchange(
                JobMessagingContract.EXCHANGE,
                true,
                false
        );
    }

    @Bean("workerQueue")
    public Queue workerQueue() {
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

    private Queue retryQueue(
            String queueName,
            int delayMs
    ) {
        return QueueBuilder
                .durable(queueName)
                .withArgument(
                        "x-message-ttl",
                        delayMs
                )
                .withArgument(
                        "x-dead-letter-exchange",
                        JobMessagingContract.EXCHANGE
                )
                .withArgument(
                        "x-dead-letter-routing-key",
                        JobMessagingContract.EXECUTE_ROUTING_KEY
                )
                .build();
    }

    @Bean("retry1Queue")
    public Queue retry1Queue() {
        return retryQueue(
                JobMessagingContract.RETRY_1_QUEUE,
                5_000
        );
    }

    @Bean("retry2Queue")
    public Queue retry2Queue() {
        return retryQueue(
                JobMessagingContract.RETRY_2_QUEUE,
                15_000
        );
    }

    @Bean("retry3Queue")
    public Queue retry3Queue() {
        return retryQueue(
                JobMessagingContract.RETRY_3_QUEUE,
                45_000
        );
    }
    @Bean("deadLetterQueue")
    public Queue deadLetterQueue() {
        return QueueBuilder
                .durable(JobMessagingContract.DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    public Binding workerBinding(
            @Qualifier("workerQueue") Queue workerQueue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder
                .bind(workerQueue)
                .to(jobsExchange)
                .with(JobMessagingContract.EXECUTE_ROUTING_KEY);
    }

    @Bean
    public Binding retry1Binding(
            @Qualifier("retry1Queue") Queue queue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder.bind(queue)
                .to(jobsExchange)
                .with(JobMessagingContract.RETRY_1_ROUTING_KEY);
    }

    @Bean
    public Binding retry2Binding(
            @Qualifier("retry2Queue") Queue queue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder.bind(queue)
                .to(jobsExchange)
                .with(JobMessagingContract.RETRY_1_ROUTING_KEY);
    }

    @Bean
    public Binding retry3Binding(
            @Qualifier("retry3Queue") Queue queue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder.bind(queue)
                .to(jobsExchange)
                .with(JobMessagingContract.RETRY_1_ROUTING_KEY);
    }
    @Bean
    public Binding deadLetterBinding(
            @Qualifier("deadLetterQueue") Queue deadLetterQueue,
            DirectExchange jobsExchange
    ) {
        return BindingBuilder
                .bind(deadLetterQueue)
                .to(jobsExchange)
                .with(JobMessagingContract.DEAD_LETTER_ROUTING_KEY);
    }
}