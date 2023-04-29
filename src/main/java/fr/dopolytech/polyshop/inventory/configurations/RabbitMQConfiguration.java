package fr.dopolytech.polyshop.inventory.configurations;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
    @Bean
    public Queue updateInventoryQueue() {
        return new Queue("updateInventoryQueue", true);
    }

    @Bean
    public Queue rollbackInventoryQueue() {
        return new Queue("rollbackInventoryQueue", true);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("orderExchange");
    }

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange("paymentExchange");
    }

    @Bean
    public TopicExchange shippingExchange() {
        return new TopicExchange("shippingExchange");
    }

    @Bean
    public Binding orderCreatedBinding(Queue updateInventoryQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(updateInventoryQueue).to(orderExchange).with("order.created");
    }

    @Bean
    public Binding paymentFailedBinding(Queue rollbackInventoryQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(rollbackInventoryQueue).to(paymentExchange).with("payment.done.failed");
    }

    @Bean
    public Binding shippingFailedBinding(Queue rollbackInventoryQueue, TopicExchange shippingExchange) {
        return BindingBuilder.bind(rollbackInventoryQueue).to(shippingExchange).with("shipping.done.failed");
    }
}
