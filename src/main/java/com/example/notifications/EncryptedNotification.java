package com.example.notifications;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/** Demonstration encoding only, NOT secure encryption. */
public class EncryptedNotification extends Notification {
    private boolean lastSuccessful;
    public EncryptedNotification(MessageSender sender) { super(sender); }
    public boolean wasLastSuccessful() { return lastSuccessful; }
    public static String encode(String plainText) {
        return Base64.getEncoder().encodeToString(Objects.requireNonNull(plainText).getBytes(StandardCharsets.UTF_8));
    }
    @Override public void send(String recipient, String message) {
        lastSuccessful = sender.isAvailable() && sender.sendMessage(recipient, "Encoded notification", encode(message));
        if (!lastSuccessful) System.out.println("[Encrypted] Delivery failed.");
    }
}
