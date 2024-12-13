package com.app.homiefy;

public class Property {
    private String imageUrl;
    private long uploadTime;

    // Constructor mặc định cần thiết cho Firebase
    public Property() {
    }

    // Getters and Setters
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }
}