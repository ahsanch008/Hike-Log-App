package com.example.myapplication.model;

public class WildlifeSighting {
    private long id;
    private long hikeId;
    private String animalName;
    private String species;
    private int quantity;
    private String notes;
    private long timestamp;

    public WildlifeSighting(long id, long hikeId, String animalName, String species, int quantity, String notes, long timestamp) {
        this.id = id;
        this.hikeId = hikeId;
        this.animalName = animalName;
        this.species = species;
        this.quantity = quantity;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public long getHikeId() { return hikeId; }
    public String getAnimalName() { return animalName; }
    public String getSpecies() { return species; }
    public int getQuantity() { return quantity; }
    public String getNotes() { return notes; }
    public long getTimestamp() { return timestamp; }
}
