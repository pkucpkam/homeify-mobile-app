package com.app.homiefy.chat;

public class Chat {
    private String chatId;
    private String userName;
    private String otherUser;  // Thêm trường otherUser
    private String lastMessage;
    private String lastMessageSenderId;
    private String lastMessageTimestamp;
    private boolean isRead;

    public Chat(String chatId, String userName, String otherUser, String lastMessage, String lastMessageSenderId, String lastMessageTimestamp, boolean isRead) {
        this.chatId = chatId;
        this.userName = userName;
        this.otherUser = otherUser;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.isRead = isRead;
    }

    public Chat(String chatId, String userName, String lastMessage, String lastMessageSenderId, String lastMessageTimestamp, boolean isRead) {
        this.chatId = chatId;
        this.userName = userName;
        this.lastMessage = lastMessage;
        this.lastMessageSenderId = lastMessageSenderId;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.isRead = isRead;
    }

    public Chat() {
    }

    // Getters
    public String getChatId() { return chatId; }
    public String getUserName() { return userName; }
    public String getOtherUser() { return otherUser; }
    public String getLastMessage() { return lastMessage; }
    public String getLastMessageTimestamp() { return lastMessageTimestamp; }
    public boolean isRead() { return isRead; }
    public String getLastMessageSenderId() { return lastMessageSenderId; }
}