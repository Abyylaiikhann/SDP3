package com.example.notifications;

/** Implementor in the Bridge pattern. */
public interface MessageSender {
    boolean sendMessage(String recipient, String title, String body);
    boolean isAvailable();
    String getChannelName();
}
