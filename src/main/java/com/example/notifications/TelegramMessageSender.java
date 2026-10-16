package com.example.notifications;

public class TelegramMessageSender extends AbstractMessageSender {
    @Override public String getChannelName() { return "Telegram"; }
    @Override public boolean sendMessage(String recipient, String title, String body) {
        if (!ready(recipient, body)) return false;
        System.out.printf("[Telegram] To: %s | *%s* | %s%n", recipient, title, body);
        return true;
    }
}
