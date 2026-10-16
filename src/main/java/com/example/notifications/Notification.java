package com.example.notifications;

import java.util.Objects;

/** Abstraction in Bridge. Holds the implementor by composition. */
public abstract class Notification {
    protected MessageSender sender;
    protected Notification(MessageSender sender) { setSender(sender); }
    public void setSender(MessageSender sender) { this.sender = Objects.requireNonNull(sender, "sender"); }
    public MessageSender getSender() { return sender; }
    public abstract void send(String recipient, String message);
}
