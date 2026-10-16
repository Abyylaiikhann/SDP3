package com.example.notifications;

public class EmailMessageSender extends AbstractMessageSender {
    @Override public String getChannelName() { return "Email"; }
    @Override public boolean sendMessage(String recipient, String title, String body) {
        if (!ready(recipient, body)) return false;
        System.out.printf("[Email] To: %s%nSubject: %s%n%s%n-- Notification Gateway%n", recipient, title, body);
        return true;
    }
}
