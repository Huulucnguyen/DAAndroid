package com.example.daandroid.Model;

public class User {
    private String username;
    private String id;
    private String ImageURL;
    private String Status;

    public User(String username, String id, String imageURL,String status) {
        this.username = username;
        this.id = id;
        ImageURL = imageURL;
        this.Status = status;
    }
    public User(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }
}
