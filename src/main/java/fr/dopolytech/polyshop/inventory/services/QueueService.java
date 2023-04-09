package fr.dopolytech.polyshop.inventory.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class QueueService {
    private final RabbitTemplate rabbitTemplate;

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpdate(String message) {
        rabbitTemplate.convertAndSend("inventoryExchange", "inventory.update", message);
    }
}
