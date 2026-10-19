package com.example.notifications;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class NotificationGatewayTest {
    static class SpySender implements MessageSender {
        boolean available = true;
        boolean result = true;
        int calls;
        String recipient, title, body;
        @Override public boolean isAvailable() { return available; }
        @Override public String getChannelName() { return "Spy"; }
        @Override public boolean sendMessage(String to, String subject, String content) {
            calls++; recipient = to; title = subject; body = content;
            return result;
        }
    }
    @Test void successfulUrgentDelivery() {
        SpySender spy = new SpySender(); UrgentNotification n = new UrgentNotification(spy);
        n.send("user", "hello");
        assertTrue(n.wasLastSuccessful()); assertEquals("[URGENT] hello", spy.body);
    }
    @Test void unavailablePrimaryNeverCalled() {
        SpySender spy = new SpySender(); spy.available = false;
        UrgentNotification n = new UrgentNotification(spy); n.send("user", "hello");
        assertFalse(n.wasLastSuccessful()); assertEquals(0, spy.calls);
    }
    @Test void unavailablePrimaryFallsBack() {
        SpySender primary = new SpySender(), fallback = new SpySender(); primary.available = false;
        UrgentNotification n = new UrgentNotification(primary, fallback); n.send("user", "hello");
        assertTrue(n.wasLastSuccessful()); assertEquals(0, primary.calls); assertEquals(1, fallback.calls);
    }
    @Test void failedPrimaryAlsoFallsBack() {
        SpySender primary = new SpySender(), fallback = new SpySender(); primary.result = false;
        UrgentNotification n = new UrgentNotification(primary, fallback); n.send("user", "hello");
        assertTrue(n.wasLastSuccessful()); assertEquals(1, primary.calls); assertEquals(1, fallback.calls);
    }
    @Test void smsAt160CharacterBoundary() {
        SmsMessageSender sms = new SmsMessageSender();
        assertTrue(sms.sendMessage("+7701", "title", "x".repeat(160)));
        assertFalse(sms.sendMessage("+7701", "title", "x".repeat(161)));
    }
    @Test void encryptedBodyIsBase64() {
        SpySender spy = new SpySender(); EncryptedNotification n = new EncryptedNotification(spy);
        n.send("person", "Secret!");
        assertEquals("U2VjcmV0IQ==", spy.body); assertTrue(n.wasLastSuccessful());
    }
    @Test void batchSummaryCountsFailures() {
        SpySender spy = new SpySender() {
            @Override public boolean sendMessage(String r, String t, String b) {
                super.sendMessage(r, t, b); return !"bad".equals(r);
            }
        };
        ScheduledBatchNotification n = new ScheduledBatchNotification(spy, List.of("a", "bad", "c"));
        n.send("ignored", "news");
        assertEquals(new ScheduledBatchNotification.BatchSummary(3, 2, 1), n.getLastSummary());
        assertEquals(3, spy.calls);
    }
    @Test void runtimeSenderSwitching() {
        SpySender first = new SpySender(), second = new SpySender();
        Notification n = new UrgentNotification(first); n.send("user", "one");
        n.setSender(second); n.send("user", "two");
        assertEquals(1, first.calls); assertEquals(1, second.calls);
    }
    @Test void newPushSenderWorksWithExistingUrgentNotification() {
        UrgentNotification n = new UrgentNotification(new PushNotificationSender());
        n.send("device-token", "alarm"); assertTrue(n.wasLastSuccessful());
    }
    @Test void newMarketingNotificationWorksWithExistingEmail() {
        SpySender spy = new SpySender(); MarketingNotification n = new MarketingNotification(spy);
        n.send("user", "Sale"); assertTrue(n.wasLastSuccessful());
        assertTrue(spy.body.startsWith("[PROMO]")); assertTrue(spy.body.contains("STOP"));
    }
    @Test void marketingFailureWhenProviderUnavailable() {
        SpySender spy = new SpySender(); spy.available = false;
        MarketingNotification n = new MarketingNotification(spy); n.send("user", "Sale");
        assertFalse(n.wasLastSuccessful()); assertEquals(0, spy.calls);
    }
    @Test void fallbackFailureReported() {
        SpySender primary = new SpySender(), fallback = new SpySender();
        primary.available = false; fallback.result = false;
        UrgentNotification n = new UrgentNotification(primary, fallback);
        n.send("user", "alert"); assertFalse(n.wasLastSuccessful());
    }
    @Test void kafkaEventSender() {
        assertTrue(new KafkaEventSender().sendMessage("topic-key", "title", "body"));
    }
    @Test void nullSenderRejected() {
        assertThrows(NullPointerException.class, () -> new UrgentNotification(null));
    }
    @Test
    void smsAcceptsShortMessage() {
        SmsMessageSender sms = new SmsMessageSender();

        boolean result = sms.sendMessage(
                "+77011234567",
                "Test",
                "Hello"
        );

        assertTrue(result);
    }

    @Test
    void smsRejectsEmptyRecipient() {
        SmsMessageSender sms = new SmsMessageSender();

        boolean result = sms.sendMessage(
                "",
                "Test",
                "Hello"
        );

        assertFalse(result);
    }
}
