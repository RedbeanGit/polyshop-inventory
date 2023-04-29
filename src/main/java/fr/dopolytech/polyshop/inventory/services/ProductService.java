package fr.dopolytech.polyshop.inventory.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.dopolytech.polyshop.inventory.dtos.CreateProductDto;
import fr.dopolytech.polyshop.inventory.dtos.UpdateProductDto;
import fr.dopolytech.polyshop.inventory.events.InventoryUpdatedEvent;
import fr.dopolytech.polyshop.inventory.events.InventoryUpdatedEventProduct;
import fr.dopolytech.polyshop.inventory.events.OrderEvent;
import fr.dopolytech.polyshop.inventory.events.OrderEventProduct;
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

    @RabbitListener(queues = "updateInventoryQueue")
    @Transactional
    public void onOrderCreated(String message) {
        try {
            OrderEvent orderCreatedEvent = queueService.parse(message, OrderEvent.class);
            List<Mono<InventoryUpdatedEventProduct>> productsMono = new ArrayList<Mono<InventoryUpdatedEventProduct>>();

            for (OrderEventProduct product : orderCreatedEvent.products) {
                productsMono.add(this.productRepository.findByProductId(product.productId)
                        .map(p -> {
                            if (p.quantity - product.quantity < 0) {
                                return new InventoryUpdatedEventProduct(p.productId, p.quantity, p.quantity,
                                        product.quantity, false);
                            } else {
                                return new InventoryUpdatedEventProduct(p.productId, p.quantity,
                                        p.quantity - product.quantity, product.quantity, true);
                            }
                        }));
            }

            List<InventoryUpdatedEventProduct> updatedProducts = Flux.merge(productsMono).collectList().block();

            if (updatedProducts.stream().map(updatedProduct -> updatedProduct.success).allMatch(success -> success)) {
                Flux.merge(updatedProducts.stream()
                        .map(updatedProduct -> {
                            return this.productRepository.findByProductId(updatedProduct.productId)
                                    .flatMap(product -> {
                                        product.quantity = updatedProduct.newQuantity;
                                        return this.productRepository.save(product);
                                    });
                        }).toList()).collectList().block();
            }

            InventoryUpdatedEvent inventoryUpdateEvent = new InventoryUpdatedEvent(orderCreatedEvent.orderId,
                    updatedProducts.toArray(new InventoryUpdatedEventProduct[updatedProducts.size()]));

            queueService.sendUpdate(inventoryUpdateEvent);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "rollbackInventoryQueue")
    @Transactional
    public void onOrderCancelled(String message) {
        try {
            OrderEvent orderEvent = queueService.parse(message, OrderEvent.class);
            Flux.merge(Arrays.asList(orderEvent.products)
                    .stream()
                    .map(orderEventProduct -> {
                        return this.productRepository.findByProductId(orderEventProduct.productId)
                                .flatMap(product -> {
                                    product.quantity += orderEventProduct.quantity;
                                    return this.productRepository.save(product);
                                });
                    }).toList()).collectList().block();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
