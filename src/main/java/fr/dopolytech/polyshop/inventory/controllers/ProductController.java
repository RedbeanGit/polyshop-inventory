package fr.dopolytech.polyshop.inventory.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.dopolytech.polyshop.inventory.documents.Product;
import fr.dopolytech.polyshop.inventory.repositories.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @GetMapping
    public Flux<Product> getProduct() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Product> getProductById(@PathVariable String id) {
        return productRepository.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProductById(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        return productRepository.save(product);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProductById(@PathVariable String id) {
        return productRepository.deleteById(id);
    }
}
