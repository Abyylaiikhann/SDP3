package com.example.notifications;

/** Shared simulated availability control for sender implementations. */
public abstract class AbstractMessageSender implements MessageSender {
    private boolean available = true;
    @Override public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    protected boolean ready(String recipient, String body) {
        return available && recipient != null && !recipient.isBlank() && body != null && !body.isBlank();
    }
}
