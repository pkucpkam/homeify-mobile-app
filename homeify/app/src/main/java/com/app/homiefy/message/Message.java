package com.app.homiefy.message;

public class Message {
    private String messageId;
    private String senderId;
    private String text;
    private String timestamp;

    public Message() {} // Empty constructor for Firebase

    public Message(String messageId, String senderId, String text, String timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.text = text;
        this.timestamp = timestamp;
    }

    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }
}