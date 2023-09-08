package com.tourism.apps;

public class Review {
    private String userId;
    private String userImgUrl;
    private String username;
    private float rating;
    private String dateSubmitted;
    private String reviewDescription;

    // Constructors, getters, and setters
    public Review() {
        // Default constructor required for Firebase
    }

    public Review(String userId, String userImgUrl, String username, float rating, String dateSubmitted, String reviewDescription) {
        this.userId = userId;
        this.userImgUrl = userImgUrl;
        this.username = username;
        this.rating = rating;
        this.dateSubmitted = dateSubmitted;
        this.reviewDescription = reviewDescription;
    }

    // Getters and setters for all fields

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImgUrl() {
        return userImgUrl;
    }

    public void setUserImgUrl(String userImgUrl) {
        this.userImgUrl = userImgUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getReviewDescription() {
        return reviewDescription;
    }

    public void setReviewDescription(String reviewDescription) {
        this.reviewDescription = reviewDescription;
    }
}
