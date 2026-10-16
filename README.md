# Assignment 3 — Multi-Channel Notification Gateway

**Course:** Software Design Patterns  
**Pattern:** Bridge  
**Language:** Java 17+  
**Build:** Maven  
**External APIs:** None (all providers are simulations)

## How to run

Install Java 17+ and Maven 3.9+.

```bash
mvn clean test
mvn exec:java
```

If Maven is unavailable, compile and run the demo directly:

```bash
mkdir -p out
javac --release 17 -d out src/main/java/com/example/notifications/*.java
java -cp out com.example.notifications.Main
```

JUnit tests still need Maven (or JUnit 5 jars + JUnit platform launcher).

## Requirements coverage

| Requirement | Where |
|---|---|
| Implementor interface | `MessageSender` |
| Telegram, Email, SMS, Kafka providers | `*MessageSender`, `KafkaEventSender` |
| Urgent with fallback | `UrgentNotification` |
| Encoded message | `EncryptedNotification` |
| Batch + summary | `ScheduledBatchNotification` |
| Runtime switching | `Notification.setSender`, `Main` scenario 4 |
| New provider | `PushNotificationSender` |
| New type | `MarketingNotification` |
| Six mandatory demo cases | `Main` scenarios 1–6 |
| JUnit 5 tests | `NotificationGatewayTest` (14 tests) |
| UML | [`uml.puml`](uml.puml) (PlantUML source) |

## Bridge explained (defense answers)

**What are the two independent dimensions?** Notification behavior (Urgent, Encrypted, ScheduledBatch, Marketing) and delivery technology (Telegram, Email, SMS, Kafka, Push).

**Where exactly is the Bridge?** The `protected MessageSender sender` reference inside `Notification` is the bridge. Notification subclasses delegate to the `MessageSender` interface rather than hard-coding a provider. `setSender` changes that implementor at runtime.

**Why is inheritance problematic without Bridge?** A class per combination (e.g. `UrgentTelegramNotification`, `EncryptedSmsNotification`) grows as N × M. With Bridge the number of main concrete classes grows approximately as N + M, and each dimension can change independently.

**What happens when a new provider is added?** Implement `MessageSender` (in this project, optionally extend `AbstractMessageSender`) and pass the new object to any existing `Notification`. No notification subclasses are edited. `PushNotificationSender` demonstrates this.

**What happens when a new notification type is added?** Extend `Notification` and implement `send`; use any pre-existing `MessageSender`. No senders are edited. `MarketingNotification` demonstrates this.

**Why Bridge rather than Adapter?** Bridge is a deliberate architectural separation of independently varying abstraction and implementation; Adapter usually converts one existing incompatible interface into another.

## Design notes and limitations

- `UrgentNotification` prefixes messages with `[URGENT]`, checks availability and tries the fallback if the primary is unavailable **or rejects delivery**; no `instanceof` checks.
- `EncryptedNotification` uses **Base64 encoding only** to satisfy the assignment's simulated transformation. Base64 does **not** protect confidentiality; real security would require authenticated encryption.
- `ScheduledBatchNotification.send(recipient, message)` intentionally ignores the `recipient` argument because the batch maintains its own recipient collection. Its `BatchSummary` stores success and failure counts.
- SMS enforces a maximum of 160 Unicode code points for the **body**. The title is ignored by SMS. (Production SMS may impose encoding-specific segment limits.)
- A provider returns `false` when unavailable or when recipient/body validation fails.
- `Main` prints each required scenario; sender methods print simulated transport-specific output.

## UML

Open `uml.puml` in a PlantUML editor (or the PlantUML VS Code extension) to view the class diagram. Main structural links: inheritance between notification types, implementation/inheritance among senders, and `Notification o--> MessageSender` (has-a Bridge), plus `UrgentNotification o--> MessageSender` fallback.

## Practice changes for individual defense

- **Add WhatsApp:** create `WhatsAppMessageSender extends AbstractMessageSender`, implement `getChannelName()` and `sendMessage(...)`, then inject it into a `Notification`.
- **SMS 120 characters:** change `SmsMessageSender.MAX_LENGTH` to `120` and adjust boundary tests.
- **Add SilentNotification:** extend `Notification` and define a no-output/conditional delivery policy; no provider changes required.
- **Email unavailable:** call `email.setAvailable(false)` and use `new UrgentNotification(email, sms)`.
- **Another fallback:** expand the urgent implementation to accept an ordered list of `MessageSender` objects; iterate via the interface.
- **Runtime switch:** call `notification.setSender(otherSender)`.

## Repository submission

Upload this folder to a GitHub repository, then submit the GitHub link on Moodle. Explain the design in your own words during the individual defense.
