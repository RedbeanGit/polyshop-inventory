package fr.dopolytech.polyshop.inventory.events;

public class InventoryUpdatedEventProduct {
    public String productId;
    public Integer oldQuantity;
    public Integer newQuantity;
    public Integer changeRequested;
    public Boolean success;

    public InventoryUpdatedEventProduct(String productId, int oldQuantity, int newQuantity, int changeRequested,
            boolean success) {
        this.productId = productId;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
        this.success = success;
    }
}
