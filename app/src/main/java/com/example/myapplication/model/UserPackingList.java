package com.example.myapplication.model;

public class UserPackingList {
    private long id;
    private long userId;
    private long hikeId;
    private long itemId;
    private String itemName;
    private String category;
    private boolean isPacked;
    private int quantity;

    public UserPackingList(long id, long userId, long hikeId, long itemId, String itemName, String category, boolean isPacked, int quantity) {
        this.id = id;
        this.userId = userId;
        this.hikeId = hikeId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.category = category;
        this.isPacked = isPacked;
        this.quantity = quantity;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public long getHikeId() { return hikeId; }
    public long getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public boolean isPacked() { return isPacked; }
    public int getQuantity() { return quantity; }
}
