package fr.dopolytech.polyshop.inventory.events;

public class InventoryUpdatedEventProduct {
    public String productId;
    public int oldQuantity;
    public int newQuantity;
    public boolean isSuccessful;

    public InventoryUpdatedEventProduct(String productId, int oldQuantity, int newQuantity, boolean isSuccessful) {
        this.productId = productId;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.isSuccessful = isSuccessful;
    }
}
