package fr.dopolytech.polyshop.inventory.events;

public class InventoryUpdatedEvent {
    public String orderId;
    public InventoryUpdatedEventProduct[] products;

    public InventoryUpdatedEvent(String orderId, InventoryUpdatedEventProduct[] products) {
        this.orderId = orderId;
        this.products = products;
    }
}
