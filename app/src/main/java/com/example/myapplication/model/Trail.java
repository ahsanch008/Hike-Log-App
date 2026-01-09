package com.example.myapplication.model;

public class Trail {
    private long id;
    private String name;
    private String location;
    private String difficulty;
    private double distanceKm;
    private int estimatedTimeMin;
    private int elevationM;
    private String description;
    private String imageName;

    public Trail(long id, String name, String location, String difficulty, double distanceKm, int estimatedTimeMin, int elevationM, String description, String imageName) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.difficulty = difficulty;
        this.distanceKm = distanceKm;
        this.estimatedTimeMin = estimatedTimeMin;
        this.elevationM = elevationM;
        this.description = description;
        this.imageName = imageName;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDifficulty() { return difficulty; }
    public double getDistanceKm() { return distanceKm; }
    public int getEstimatedTimeMin() { return estimatedTimeMin; }
    public int getElevationM() { return elevationM; }
    public String getDescription() { return description; }
    public String getImageName() { return imageName; }
}
