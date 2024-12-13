package com.app.homiefy.room;

public class Room {
    private String roomName;
    private String rentPrice;
    private String area;
    private String address;
    private String rules;
    private String startDate;
    private String endDate;
    private String contactInfo;
    private String imageUrl;

    public Room() {
    }

    public Room(String roomName, String rentPrice, String area, String address, String rules, String startDate, String endDate, String contactInfo, String imageUrl) {
        this.roomName = roomName;
        this.rentPrice = rentPrice;
        this.area = area;
        this.address = address;
        this.rules = rules;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contactInfo = contactInfo;
        this.imageUrl = imageUrl;
    }

    // Getters and Setters
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
}
