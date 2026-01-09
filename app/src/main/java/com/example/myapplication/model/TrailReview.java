package com.example.myapplication.model;

public class TrailReview {
    private long id;
    private long trailId;
    private long userId;
    private String userName;
    private int rating;
    private String comment;
    private long createdAt;

    public TrailReview(long id, long trailId, long userId, String userName, int rating, String comment, long createdAt) {
        this.id = id;
        this.trailId = trailId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getTrailId() { return trailId; }
    public long getUserId() { return userId; }
    public String getUserName() { return userName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public long getCreatedAt() { return createdAt; }
}
