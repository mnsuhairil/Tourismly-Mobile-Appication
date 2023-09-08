package com.tourism.apps;

public class User {
    private String email;
    private String password;
    private String username;
    private String imgUrl;
    // Default constructor required for Firebase Realtime Database
    public User() {
    }

    public User(String email,String password,String username,String imgUrl) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
