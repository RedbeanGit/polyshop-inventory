package fr.dopolytech.polyshop.inventory.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import fr.dopolytech.polyshop.inventory.models.Product;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, String> {
    public Mono<Product> findByProductId(String productId);

    public Mono<Void> deleteByProductId(String productId);
}
