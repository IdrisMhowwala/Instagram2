package com.example.firebasedemo.Domain;

public class Post {

    private String imageUrl;
    private String postId;
    private String Publisher;
    private String description;

    public Post() {
    }

    public Post(String imageUrl, String postId, String publisher, String description) {
        this.imageUrl = imageUrl;
        this.postId = postId;
        Publisher = publisher;
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String publisher) {
        Publisher = publisher;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
