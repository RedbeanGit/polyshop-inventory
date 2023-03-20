package fr.dopolytech.polyshop.inventory.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Product {
    @Id
    public String id;
    public double price;
    public int quantity;

    public void setId(String id) {
        this.id = id;
    }
}
