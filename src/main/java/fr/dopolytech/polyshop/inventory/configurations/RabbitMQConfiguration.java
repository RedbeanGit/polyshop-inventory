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
    public Queue orderQueue() {
        return new Queue("inventoryOrderQueue", true);
    }

    @Bean
    public TopicExchange cartExchange() {
        return new TopicExchange("orderExchange");
    }

    @Bean
    public Binding orderBinding(Queue inventoryQueue, TopicExchange cartExchange) {
        return BindingBuilder.bind(inventoryQueue).to(cartExchange).with("order.process");
    }
}
