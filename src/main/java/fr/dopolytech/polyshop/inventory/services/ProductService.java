package fr.dopolytech.polyshop.inventory.services;

import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.dopolytech.polyshop.inventory.documents.CartPurchase;
import fr.dopolytech.polyshop.inventory.documents.Product;
import fr.dopolytech.polyshop.inventory.repositories.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final QueueService queueService;

    public ProductService(ProductRepository productRepository, QueueService queueService) {
        this.productRepository = productRepository;
        this.queueService = queueService;
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> getProductById(String id) {
        return productRepository.findById(id);
    }

    public Mono<Product> saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Mono<Void> deleteById(String id) {
        return productRepository.deleteById(id);
    }

    @RabbitListener(queues = "inventoryOrderQueue")
    public void processOrder(String message) {
        System.out.println("Received message: " + message);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<CartPurchase> purchases = objectMapper.readValue(message, new TypeReference<List<CartPurchase>>() {
            });
            purchases.forEach(purchase -> {
                productRepository.findById(purchase.getProductId()).subscribe(product -> {
                    product.setQuantity(product.getQuantity() - purchase.getCount());
                    productRepository.save(product).subscribe();
                });
            });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        queueService.sendUpdate(message);
    }
}
