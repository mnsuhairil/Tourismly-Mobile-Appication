package com.tourism.apps;

public class Comment {
    private String text;
    private String userId; // If you want to track user IDs
    // Add other properties as needed

    public Comment() {
        // Default constructor required for Firebase
    }

    public Comment(String text, String userId) {
        this.text = text;
        this.userId = userId;
        // Initialize other properties here if needed
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Add getters and setters for other properties if needed
}
