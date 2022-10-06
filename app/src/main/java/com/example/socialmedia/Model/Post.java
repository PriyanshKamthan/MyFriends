package com.example.socialmedia.Model;

public class Post {
    private String postId;
    private String imageurl;
    private String description;
    private String publisher;

    public Post() {
    }

    public Post(String postId, String imageurl, String description, String publisher) {
        this.postId = postId;
        this.imageurl = imageurl;
        this.description = description;
        this.publisher = publisher;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
