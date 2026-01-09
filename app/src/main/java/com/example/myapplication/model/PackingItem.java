package com.example.myapplication.model;

public class PackingItem {
    private long id;
    private Long trailId;
    private String itemName;
    private String category;
    private boolean isEssential;
    private int quantityDefault;

    public PackingItem(long id, Long trailId, String itemName, String category, boolean isEssential, int quantityDefault) {
        this.id = id;
        this.trailId = trailId;
        this.itemName = itemName;
        this.category = category;
        this.isEssential = isEssential;
        this.quantityDefault = quantityDefault;
    }

    public long getId() { return id; }
    public Long getTrailId() { return trailId; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public boolean isEssential() { return isEssential; }
    public int getQuantityDefault() { return quantityDefault; }
}
