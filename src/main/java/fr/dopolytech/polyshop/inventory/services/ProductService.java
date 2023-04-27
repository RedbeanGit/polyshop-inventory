package fr.dopolytech.polyshop.inventory.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.dopolytech.polyshop.inventory.dtos.CreateProductDto;
import fr.dopolytech.polyshop.inventory.dtos.UpdateProductDto;
import fr.dopolytech.polyshop.inventory.events.InventoryUpdatedEvent;
import fr.dopolytech.polyshop.inventory.events.InventoryUpdatedEventProduct;
import fr.dopolytech.polyshop.inventory.events.OrderCreatedEvent;
import fr.dopolytech.polyshop.inventory.events.OrderCreatedEventProduct;
import fr.dopolytech.polyshop.inventory.models.Product;
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

    public Mono<Product> getProduct(String productId) {
        return productRepository.findByProductId(productId);
    }

    public Flux<Product> getProducts() {
        return productRepository.findAll();
    }

    public Mono<Product> createProduct(CreateProductDto dto) {
        return productRepository.save(new Product(dto.productId, dto.quantity));
    }

    public Mono<Product> updateProduct(String productId, UpdateProductDto dto) {
        return productRepository.findByProductId(productId)
                .flatMap(product -> {
                    product.quantity = dto.quantity;
                    return productRepository.save(product);
                });
    }

    public Mono<Void> deleteProduct(String productId) {
        return productRepository.deleteByProductId(productId);
    }

    @RabbitListener(queues = "inventoryQueue")
    public void onOrderCreated(String message) {
        try {
            OrderCreatedEvent orderCreatedEvent = queueService.parse(message, OrderCreatedEvent.class);
            List<Mono<InventoryUpdatedEventProduct>> productsMono = new ArrayList<>();

            for (OrderCreatedEventProduct product : orderCreatedEvent.products) {
                productsMono.add(productRepository.findByProductId(product.productId)
                        .flatMap(p -> {
                            if (p.quantity - product.quantity < 0) {
                                return Mono.just(new InventoryUpdatedEventProduct(p.productId, p.quantity, p.quantity,
                                        product.quantity, false));
                            } else {
                                p.quantity -= product.quantity;
                                return this.productRepository
                                        .save(p)
                                        .then(Mono.just(new InventoryUpdatedEventProduct(p.productId,
                                                p.quantity + product.quantity, p.quantity, product.quantity, true)));
                            }
                        }));
            }

            List<InventoryUpdatedEventProduct> updatedProducts = Flux.merge(productsMono).collectList().block();
            InventoryUpdatedEvent inventoryUpdateEvent = new InventoryUpdatedEvent(orderCreatedEvent.orderId,
                    updatedProducts.toArray(new InventoryUpdatedEventProduct[updatedProducts.size()]));

            queueService.sendUpdate(inventoryUpdateEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
