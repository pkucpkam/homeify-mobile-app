package com.app.homiefy.review;

public class Review {
    private String userId;
    private String review;
    private float rating;
    private String reviewerName; // Add this field for the reviewer's name

    public Review() {
        // Default constructor required for Firestore
    }

    public Review(String userId, String review, float rating) {
        this.userId = userId;
        this.review = review;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public String getReview() {
        return review;
    }

    public float getRating() {
        return rating;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }
}


