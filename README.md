# Zest

Zest is a zero-trust, domain-driven design (DDD), event-sourcing, and event-driven framework built in Java. It leverages envelope encryption, strict typing, and a vertical slice architecture to provide a secure and extensible foundation for building applications. Key features include robust authentication options: passwords, TOTP (MFA), PassKeys, and social logins.

## Setup

1. **Clone the Repository**:
   git clone https://github.com/eoslick/Zest.git
   cd Zest
2. **Build The Project**
   mvn compile
3. **Run the Demo**
   mvn exec:java
   Output: "User created and event published!"

## Authentication

Zest supports multiple authentication methods, managed via `AuthenticationService`:

### Passwords

- **Enable**: Use `CreateUser` with a password:
  var createUser = new CreateUser(users, userEvents, authRepo);
  createUser.execute(userId, tenantId, accountId, email, "securePass123", role);

- **Login**: Authenticate with `authenticatePassword`:
  var result = authService.authenticatePassword(userId, tenantId, "securePass123");


### TOTP (MFA)

- **Enable**: After creating a user, enable TOTP with `EnableMFA`:

  var enableMFA = new EnableMFA(authRepo);
  String totpSecret = enableMFA.execute(userId, tenantId); // Use secret in Google Authenticator


- **Login**: After password auth returns `MFARequired`, use `authenticateTOTP`:
  String totpCode = TOTPVerifier.generateCode(totpSecret);
  var result = authService.authenticateTOTP(userId, tenantId, totpCode);



### PassKeys

- **Enable**: Register a PassKey with `EnablePassKey`:

String challenge = authService.generatePassKeyChallenge();
String signedChallenge = "signed-" + publicKey; // Client-side signing
var result = authService.authenticatePassKey(userId, tenantId, signedChallenge);


### Social Logins (Google OAuth)

- **Enable**: Link a Google account with `EnableSocialLogin`:

  var enableSocialLogin = new EnableSocialLogin(authRepo);
  enableSocialLogin.execute(userId, tenantId, "google", "id-token-from-google");

- **Login**: Authenticate with a Google ID token:

  var result = authService.authenticateSocial(userId, tenantId, "google", "id-token-from-google");


## License

"Zest" is dual-licensed:
- **Open Source**: GNU General Public License v3 (GPL-3.0) - Copyright (c) 2025 Evan Oslick. See [LICENSE](LICENSE) for details. Use freely in open-source projects that comply with GPL v3.
- **Commercial Use**: For non-open-source projects, a commercial license is required. Contact Evan Oslick at [eoslick@example.com] for licensing terms.

## Next Steps

- Enhance PassKey with real FIDO2/WebAuthn support.
- Add more social providers (e.g., Facebook).
- Implement file-based persistence improvements.

