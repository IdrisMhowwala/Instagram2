package com.example.firebasedemo.Domain;

public class User {
    private String name;
    private String email;
    private String userName;
    private String bio;
    private String imageurl;
    private String id;


    public User() {
    }

    public User(String name, String email, String userName, String bio, String imageurl, String id) {
        this.name = name;
        this.email = email;
        this.userName = userName;
        this.bio = bio;
        this.imageurl = imageurl;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
