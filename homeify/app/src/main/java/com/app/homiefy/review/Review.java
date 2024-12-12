package com.app.homiefy.review;

public class Review {
    private float rating;
    private String comment;

    // Constructor
    public Review() {
    }

    public Review(float rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
