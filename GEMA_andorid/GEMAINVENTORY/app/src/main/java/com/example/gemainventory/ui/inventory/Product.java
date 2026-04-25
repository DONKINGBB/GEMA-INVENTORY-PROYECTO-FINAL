package com.example.gemainventory.ui.inventory;

public class Product {
    private String id;
    private String name;
    private String sku;
    private int quantity;
    private int minStock;
    private double purchasePrice;
    private double salePrice;
    private String description;
    private String category;
    private String imageUrl;
    private long dateAdded;

    public Product(String id, String name, String sku, int quantity, int minStock, double salePrice, String description, String category, String imageUrl) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.quantity = quantity;
        this.minStock = minStock;
        this.salePrice = salePrice;
        this.description = description;
        this.category = category;
        this.imageUrl = imageUrl;
        this.dateAdded = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public int getQuantity() { return quantity; }
    public int getMinStock() { return minStock; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSalePrice() { return salePrice; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public long getDateAdded() { return dateAdded; }
    public void setDateAdded(long dateAdded) { this.dateAdded = dateAdded; }

    public enum StockStatus {
        IN_STOCK,
        LOW_STOCK,
        OUT_OF_STOCK
    }

    public StockStatus getStatus() {
        if (quantity <= 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (quantity <= minStock) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}