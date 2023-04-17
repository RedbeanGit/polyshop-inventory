package fr.dopolytech.polyshop.inventory.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.inventory.events.InventoryUpdatedEvent;

@Component
public class QueueService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpdate(InventoryUpdatedEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("inventoryExchange", "inventory.update", stringify(event));
    }

    public String stringify(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public <T> T parse(String json, Class<T> type) throws JsonProcessingException {
        return mapper.readValue(json, type);
    }
}
