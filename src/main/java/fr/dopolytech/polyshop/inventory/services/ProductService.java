package fr.dopolytech.polyshop.inventory.services;

import java.util.Arrays;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.dopolytech.polyshop.inventory.dtos.CreateProductDto;
import fr.dopolytech.polyshop.inventory.dtos.UpdateProductDto;
import fr.dopolytech.polyshop.inventory.models.PolyshopEvent;
import fr.dopolytech.polyshop.inventory.models.PolyshopEventProduct;
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
            PolyshopEvent event = this.queueService.parse(message);
            List<PolyshopEventProduct> eventProducts = Arrays.asList(event.products);
            Flux.merge(eventProducts.stream()
                    .map(eventProduct -> this.productRepository.findByProductId(eventProduct.id)
                            .map(product -> product.quantity - eventProduct.quantity >= 0))
                    .toList()).collectList().flatMap(results -> {
                        if (results.stream().allMatch(result -> result)) {
                            return Flux.merge(eventProducts.stream()
                                    .map(eventProduct -> this.productRepository.findByProductId(eventProduct.id)
                                            .flatMap(product -> {
                                                product.quantity -= eventProduct.quantity;
                                                return this.productRepository.save(product);
                                            }))
                                    .toList()).collectList().then(Mono.just(true));
                        } else {
                            return Mono.just(false);
                        }
                    })
                    .map(result -> {
                        try {
                            if (result) {
                                this.queueService.sendUpdateSuccess(event);
                            } else {
                                this.queueService.sendUpdateFailed(event);
                            }
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        return Mono.empty();
                    }).block();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "rollbackInventoryQueue")
    @Transactional
    public void onRollback(String message) {
        try {
            PolyshopEvent event = this.queueService.parse(message);
            List<PolyshopEventProduct> eventProducts = Arrays.asList(event.products);
            Flux.merge(eventProducts.stream()
                    .map(eventProduct -> this.productRepository.findByProductId(eventProduct.id)
                            .flatMap(product -> {
                                product.quantity += eventProduct.quantity;
                                return this.productRepository.save(product);
                            }))
                    .toList()).collectList().block();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
