package com.example.notifications;

/** Added provider: no Notification subclasses need modification. */
public class PushNotificationSender extends AbstractMessageSender {
    @Override public String getChannelName() { return "Push"; }
    @Override public boolean sendMessage(String recipient, String title, String body) {
        if (!ready(recipient, body)) return false;
        System.out.printf("[Push] Device: %s | Alert: %s | %s%n", recipient, title, body);
        return true;
    }
}
