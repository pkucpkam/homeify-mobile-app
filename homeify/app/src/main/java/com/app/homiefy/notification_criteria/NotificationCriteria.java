package com.app.homiefy.notification_criteria;

import java.util.List;

public class NotificationCriteria {
    private String userId;  // ID của người dùng đăng ký nhận tin
    private String location;
    private Double minPrice;
    private Double maxPrice;
    private Double minArea;
    private Double maxArea;
    private List<String> amenities;
    private String otherRequirements;

    public NotificationCriteria() {
    }

    // Constructor
    public NotificationCriteria(String userId, String location, Double minPrice, Double maxPrice,
                                Double minArea, Double maxArea, List<String> amenities,
                                String otherRequirements) {
        this.userId = userId;
        this.location = location;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.amenities = amenities;
        this.otherRequirements = otherRequirements;
    }

    // Getter và Setter cho userId
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // Getter và Setter cho các trường khác
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getMinArea() {
        return minArea;
    }

    public void setMinArea(Double minArea) {
        this.minArea = minArea;
    }

    public Double getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(Double maxArea) {
        this.maxArea = maxArea;
    }

    public List<String> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    public String getOtherRequirements() {
        return otherRequirements;
    }

    public void setOtherRequirements(String otherRequirements) {
        this.otherRequirements = otherRequirements;
    }
}
