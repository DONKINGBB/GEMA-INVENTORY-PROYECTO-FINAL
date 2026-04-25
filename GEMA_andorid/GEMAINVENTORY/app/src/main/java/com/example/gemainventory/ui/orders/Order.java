package com.example.gemainventory.ui.orders;

public class Order {
    private String id;
    private String clientName;
    private String date;
    private double amount;
    private OrderStatus status;

    // Enum para los estados del pedido
    public enum OrderStatus {
        PENDIENTE,
        ENVIADO,
        ENTREGADO
    }

    public Order(String id, String clientName, String date, double amount, OrderStatus status) {
        this.id = id;
        this.clientName = clientName;
        this.date = date;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public String getId() { return id; }
    public String getClientName() { return clientName; }
    public String getDate() { return date; }
    public double getAmount() { return amount; }
    public OrderStatus getStatus() { return status; }
}