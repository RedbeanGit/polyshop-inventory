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
    public Queue inventoryQueue() {
        return new Queue("inventoryQueue", true);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("orderExchange");
    }

    @Bean
    public Binding orderBinding(Queue inventoryQueue, TopicExchange exchange) {
        return BindingBuilder.bind(inventoryQueue).to(exchange).with("order.created");
    }
}
