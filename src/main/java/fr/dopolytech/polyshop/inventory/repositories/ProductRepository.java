package fr.dopolytech.polyshop.inventory.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import fr.dopolytech.polyshop.inventory.documents.Product;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, String> {

}
