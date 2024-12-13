package com.app.homiefy.room;

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

    public Room() {}

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
}
