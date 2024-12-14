package com.app.homiefy.notification;

public class Notification {
    private String id;
    private String title;
    private String content;
    private String timestamp;
    private boolean isRead;

    public Notification() {
        // Required empty constructor for Firebase
    }

    public Notification(String id, String title, String content, String timestamp, boolean isRead) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}