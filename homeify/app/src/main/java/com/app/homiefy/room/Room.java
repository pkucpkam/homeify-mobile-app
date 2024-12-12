package com.app.homiefy.room;

import java.util.List;

public class Room {
    private RoomBasic basic;
    private RoomDetails details;

    public Room(RoomBasic basic, RoomDetails details) {
        this.basic = basic;
        this.details = details;
    }

    // Getters and setters
    public RoomBasic getBasic() { return basic; }
    public void setBasic(RoomBasic basic) { this.basic = basic; }
    public RoomDetails getDetails() { return details; }
    public void setDetails(RoomDetails details) { this.details = details; }

    // Các phương thức tiện ích
    public String getTitle() { return basic.getTitle(); }
    public String getDescription() { return basic.getDescription(); }
    public double getPrice() { return basic.getPrice(); }
    public Address getAddress() { return basic.getAddress(); }

    public static class RoomBasic {
        private String title;
        private String description;
        private double price;
        private Address address;

        // Constructor, getters, and setters
        public RoomBasic(String title, String description, double price, Address address) {
            this.title = title;
            this.description = description;
            this.price = price;
            this.address = address;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public Address getAddress() { return address; }
        public void setAddress(Address address) { this.address = address; }
    }

    public static class Address {
        private String street;
        private String ward;
        private String district;
        private String city;

        // Constructor, getters, and setters
        public Address(String street, String ward, String district, String city) {
            this.street = street;
            this.ward = ward;
            this.district = district;
            this.city = city;
        }

        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        public String getWard() { return ward; }
        public void setWard(String ward) { this.ward = ward; }
        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        @Override
        public String toString() {
            return street + ", " + ward + ", " + district + ", " + city;
        }
    }

    public static class RoomDetails {
        private List<String> furniture;
        private List<String> utilities;
        private List<String> amenities;
        private List<String> rules;

        // Constructor, getters, and setters
        public RoomDetails(List<String> furniture, List<String> utilities, List<String> amenities, List<String> rules) {
            this.furniture = furniture;
            this.utilities = utilities;
            this.amenities = amenities;
            this.rules = rules;
        }

        public List<String> getFurniture() { return furniture; }
        public void setFurniture(List<String> furniture) { this.furniture = furniture; }
        public List<String> getUtilities() { return utilities; }
        public void setUtilities(List<String> utilities) { this.utilities = utilities; }
        public List<String> getAmenities() { return amenities; }
        public void setAmenities(List<String> amenities) { this.amenities = amenities; }
        public List<String> getRules() { return rules; }
        public void setRules(List<String> rules) { this.rules = rules; }
    }
}
