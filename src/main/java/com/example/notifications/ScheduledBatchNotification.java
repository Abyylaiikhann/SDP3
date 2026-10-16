package com.example.notifications;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ScheduledBatchNotification extends Notification {
    public record BatchSummary(int total, int successful, int failed) { }
    private final List<String> recipients = new ArrayList<>();
    private BatchSummary lastSummary = new BatchSummary(0, 0, 0);
    public ScheduledBatchNotification(MessageSender sender, Collection<String> recipients) {
        super(sender);
        this.recipients.addAll(Objects.requireNonNull(recipients));
    }
    public void addRecipient(String recipient) { recipients.add(recipient); }
    public BatchSummary getLastSummary() { return lastSummary; }
    @Override public void send(String recipient, String message) {
        // For batch notifications, the recipient parameter is intentionally ignored.
        int successes = 0;
        for (String target : recipients) {
            if (sender.isAvailable() && sender.sendMessage(target, "Scheduled batch", message)) successes++;
        }
        lastSummary = new BatchSummary(recipients.size(), successes, recipients.size() - successes);
        System.out.printf("Batch summary: Total: %d | Successful: %d | Failed: %d%n",
                lastSummary.total(), lastSummary.successful(), lastSummary.failed());
    }
}
