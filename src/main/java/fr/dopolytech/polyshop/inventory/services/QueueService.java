package fr.dopolytech.polyshop.inventory.services;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.inventory.models.PolyshopEvent;

@Component
public class QueueService {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public QueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendUpdateSuccess(PolyshopEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("inventoryExchange", "inventory.update.success", stringify(event));
    }

    public void sendUpdateFailed(PolyshopEvent event) throws JsonProcessingException {
        rabbitTemplate.convertAndSend("inventoryExchange", "inventory.update.failed", stringify(event));
    }

    public String stringify(PolyshopEvent obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    public PolyshopEvent parse(String json) throws JsonProcessingException {
        return mapper.readValue(json, PolyshopEvent.class);
    }
}
