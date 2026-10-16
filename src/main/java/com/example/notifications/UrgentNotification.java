package com.example.notifications;

import java.util.Objects;

public class UrgentNotification extends Notification {
    private MessageSender fallback;
    private boolean lastSuccessful;
    private String lastChannel;
    public UrgentNotification(MessageSender sender) { super(sender); }
    public UrgentNotification(MessageSender sender, MessageSender fallback) {
        super(sender); this.fallback = fallback;
    }
    public void setFallback(MessageSender fallback) { this.fallback = fallback; }
    public boolean wasLastSuccessful() { return lastSuccessful; }
    public String getLastChannel() { return lastChannel; }
    @Override public void send(String recipient, String message) {
        String body = "[URGENT] " + Objects.requireNonNull(message, "message");
        lastSuccessful = false;
        lastChannel = null;
        if (sender.isAvailable()) {
            lastSuccessful = sender.sendMessage(recipient, "Urgent notification", body);
            if (lastSuccessful) lastChannel = sender.getChannelName();
        }
        // Try fallback both when primary is unavailable and when delivery fails.
        if (!lastSuccessful && fallback != null && fallback.isAvailable()) {
            lastSuccessful = fallback.sendMessage(recipient, "Urgent notification", body);
            if (lastSuccessful) lastChannel = fallback.getChannelName();
        }
        if (!lastSuccessful) System.out.println("[Urgent] Delivery failed (primary and fallback).");
    }
}
