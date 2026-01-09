package com.example.myapplication.model;

public class Hike {
    private long id;
    private long userId;
    private Long trailId;
    private String trailName;
    private String difficulty;
    private long dateMillis;
    private int durationMin;
    private String notes;
    private String elevationPoints;

    public Hike(long id, long userId, Long trailId, String trailName, String difficulty, long dateMillis, int durationMin, String notes, String elevationPoints) {
        this.id = id;
        this.userId = userId;
        this.trailId = trailId;
        this.trailName = trailName;
        this.difficulty = difficulty;
        this.dateMillis = dateMillis;
        this.durationMin = durationMin;
        this.notes = notes;
        this.elevationPoints = elevationPoints;
    }

    public long getId() { return id; }
    public long getUserId() { return userId; }
    public Long getTrailId() { return trailId; }
    public String getTrailName() { return trailName; }
    public String getDifficulty() { return difficulty; }
    public long getDateMillis() { return dateMillis; }
    public int getDurationMin() { return durationMin; }
    public String getNotes() { return notes; }
    public String getElevationPoints() { return elevationPoints; }
}

