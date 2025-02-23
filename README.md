# Zest

Zest is a zero-trust, domain-driven design (DDD), event-sourcing, and event-driven framework built in Java. It leverages envelope encryption, strict typing, and a vertical slice architecture to provide a secure and extensible foundation for building applications.

## Features
- **Zero Trust**: Enforces role-based access control (RBAC) and user-based envelope encryption.
- **Domain-Driven Design**: Uses ubiquitous language (e.g., `User`, `UserId`) for clarity.
- **Event Sourcing**: Stores state changes as events, with pluggable storage (in-memory by default).
- **Event-Driven**: Publishes events via an extensible event bus.
- **JDK-Only**: Minimizes external dependencies, built with JDK 23.
- **Vertical Slice**: Organizes code by feature (e.g., `user`, `event`, `encryption`).

## Requirements
- Java Development Kit (JDK) 23
- Maven 3.6+ (for build and dependency management)

## Setup
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/yourusername/Zest.git
   cd Zest

2. **Build the Project**:
   ```bash
   mvn compile

3. **Run the Demo**:
   ```bash
   mvn exec:java
   Output: "User created and event published!"

## Usage

Create a user with the `CreateUser` use case:
```java
var encryption = new AesEncryption();
var keyManager = new InMemoryKeyManager(encryption);
var eventStore = new InMemoryEventStore(encryption, keyManager);
var eventBus = new InMemoryEventBus();
var users = new InMemoryUsers();
var userEvents = new InMemoryUserEvents(eventBus);

var createUser = new CreateUser(users, userEvents);
createUser.execute(new UserId(), new TenantId(), new AccountId(), new Email("user@example.com"), BasicRole.TENANT_ADMIN);

## Project Structure
src/
├── main/java/com/ses/zest/
│   ├── common/         # Base types (EntityId, Event)
│   ├── user/          # User management slice
│   ├── event/         # Event-sourcing and dispatching
│   ├── encryption/    # Envelope encryption logic
│   └── security/      # RBAC implementation
└── test/java/com/ses/zest/
    └── user/          # Tests for user slice
    
    
## Testing

Run tests with:
```bash
mvn test

## Contributing

- Fork the repo.
- Create a feature branch (`git checkout -b feature/your-feature`).
- Commit changes (`git commit -m "Add your feature"`).
- Push to your fork (`git push origin feature/your-feature`).
- Open a pull request.

## License

"Zest" is dual-licensed:
- **Open Source**: GNU General Public License v3 (GPL-3.0) - Copyright (c) 2025 Evan Oslick. See [LICENSE](LICENSE) for details. Use freely in open-source projects that comply with GPL v3.
- **Commercial Use**: For non-open-source projects, a commercial license is required. Contact Evan Oslick at eoslick@snakeeyessoftware.com for licensing terms.

    