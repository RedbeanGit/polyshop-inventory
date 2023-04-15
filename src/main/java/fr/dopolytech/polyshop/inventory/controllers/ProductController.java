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

import fr.dopolytech.polyshop.inventory.dtos.CreateProductDto;
import fr.dopolytech.polyshop.inventory.dtos.UpdateProductDto;
import fr.dopolytech.polyshop.inventory.models.Product;
import fr.dopolytech.polyshop.inventory.services.ProductService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Product> createProduct(@RequestBody CreateProductDto product) {
        return productService.createProduct(product);
    }

    @GetMapping
    public Flux<Product> getProduct() {
        return productService.getProducts();
    }

    @GetMapping("/{id}")
    public Mono<Product> getProductById(@PathVariable String id) {
        return productService.getProduct(id);
    }

    @PutMapping("/{id}")
    public Mono<Product> updateProductById(@PathVariable String productId, @RequestBody UpdateProductDto dto) {
        return productService.updateProduct(productId, dto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteProductById(@PathVariable String id) {
        return productService.deleteProduct(id);
    }
}
