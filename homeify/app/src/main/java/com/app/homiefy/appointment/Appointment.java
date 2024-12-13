package com.app.homiefy.appointment;

public class Appointment {
    private String name;
    private String phone;
    private String email;
    private String date;
    private String time;

    public Appointment() {
    }

    public Appointment(String name, String phone, String email, String date, String time) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.date = date;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}