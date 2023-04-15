package fr.dopolytech.polyshop.inventory.events;

public class OrderCreatedEvent {
    public String orderId;
    public OrderCreatedEventProduct[] products;
}
