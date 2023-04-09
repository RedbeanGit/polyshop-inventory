package fr.dopolytech.polyshop.inventory.documents;

public class CartPurchase {
    public String productId;
    public Long count;

    public String getProductId() {
        return productId;
    }

    public int getCount() {
        return count.intValue();
    }
}
