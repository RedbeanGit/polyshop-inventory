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
    public Binding orderCreatedBinding(Queue updateInventoryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(updateInventoryQueue).to(exchange).with("order.created");
    }

    @Bean
    public Binding orderPaymentCancelledBinding(Queue rollbackInventoryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(rollbackInventoryQueue).to(exchange).with("order.paid.cancelled");
    }

    @Bean
    public Binding orderShippingCancelledBinding(Queue rollbackInventoryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(rollbackInventoryQueue).to(exchange).with("order.shipped.cancelled");
    }
}
