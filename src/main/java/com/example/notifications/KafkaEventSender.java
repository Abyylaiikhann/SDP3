package com.example.notifications;

public class KafkaEventSender extends AbstractMessageSender {
    @Override public String getChannelName() { return "Kafka"; }
    @Override public boolean sendMessage(String recipient, String title, String body) {
        if (!ready(recipient, body)) return false;
        System.out.printf("[Kafka] Published event {topic:'notifications', key:'%s', title:'%s', payload:'%s'}%n", recipient, title, body);
        return true;
    }
}
