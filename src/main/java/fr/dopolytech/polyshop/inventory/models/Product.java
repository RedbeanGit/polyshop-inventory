package fr.dopolytech.polyshop.inventory.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Product {
    @Id
    public String id;
    public String productId;
    public int quantity;

    public Product() {

    }

    public Product(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
