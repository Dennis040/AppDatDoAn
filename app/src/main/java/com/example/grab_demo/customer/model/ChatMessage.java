package com.example.grab_demo.customer.model;

public class ChatMessage {
    private String message;
    private boolean isUser;
    private long timestamp;

    public ChatMessage(String message, boolean isUser) {
        this.message = message;
        this.isUser = isUser;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters
    public String getMessage() { return message; }
    public boolean isUser() { return isUser; }
    public long getTimestamp() { return timestamp; }
}
