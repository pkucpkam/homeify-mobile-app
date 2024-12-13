package com.app.homiefy.room;

import java.util.List;

public class Room {
    private String id;
    private String userId;
    private String roomName;
    private String rentPrice;
    private String area;
    private String address;
    private String rules;
    private String startDate;
    private String endDate;
    private String contactInfo;
    private String imageUrl;
    private long createdAt;

    // Mô tả chi tiết phòng
    private String description;

    // Danh sách tiện ích
    private List<String> amenities;

    public Room() {}

    // Getters và Setters hiện tại
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getRentPrice() { return rentPrice; }
    public void setRentPrice(String rentPrice) { this.rentPrice = rentPrice; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    // Getters và Setters mới thêm
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
}
