package com.example.notifications;

public class SmsMessageSender extends AbstractMessageSender {
    public static final int MAX_LENGTH = 160;
    @Override public String getChannelName() { return "SMS"; }
    @Override public boolean sendMessage(String recipient, String title, String body) {
        // The SMS title is not part of the transmitted message. Limit is on the body.
        if (!ready(recipient, body) || body.codePointCount(0, body.length()) > MAX_LENGTH) return false;
        System.out.printf("[SMS] To: %s | %s%n", recipient, body);
        return true;
    }
}
