package com.example.notifications;

/** Added notification style: existing senders remain untouched. */
public class MarketingNotification extends Notification {
    private boolean lastSuccessful;
    public MarketingNotification(MessageSender sender) { super(sender); }
    public boolean wasLastSuccessful() { return lastSuccessful; }
    @Override public void send(String recipient, String message) {
        String body = "[PROMO] " + message + " | Reply STOP to opt out.";
        lastSuccessful = sender.isAvailable() && sender.sendMessage(recipient, "Special offer", body);
        if (!lastSuccessful) System.out.println("[Marketing] Delivery failed.");
    }
}
