package com.example.notifications;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TelegramMessageSender telegram = new TelegramMessageSender();
        EmailMessageSender email = new EmailMessageSender();
        SmsMessageSender sms = new SmsMessageSender();
        KafkaEventSender kafka = new KafkaEventSender();
        PushNotificationSender push = new PushNotificationSender();

        System.out.println("1) Urgent + Telegram");
        new UrgentNotification(telegram, sms).send("@alex", "Server is down!");

        System.out.println("\n2) Encrypted + Email");
        new EncryptedNotification(email).send("alex@example.com", "Private message");

        System.out.println("\n3) Batch + SMS (including one failed recipient)");
        ScheduledBatchNotification batch = new ScheduledBatchNotification(sms,
                List.of("+77010000001", "+77010000002", ""));
        batch.send("ignored", "Your appointment is tomorrow.");

        System.out.println("\n4) Runtime switching: Telegram -> Email");
        Notification notification = new UrgentNotification(telegram, sms);
        notification.send("@alex", "First channel");
        notification.setSender(email);
        notification.send("alex@example.com", "Second channel");

        System.out.println("\n5) Telegram unavailable -> SMS fallback");
        telegram.setAvailable(false);
        new UrgentNotification(telegram, sms).send("+77010000001", "Fallback works");

        System.out.println("\n6) Marketing + Push");
        new MarketingNotification(push).send("device-token-123", "20% discount today");

        System.out.println("\n7) Additional independent combination: Urgent + Kafka");
        new UrgentNotification(kafka).send("billing-service", "Payment processing delayed");
    }
}
