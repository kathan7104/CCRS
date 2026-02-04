# CCRS (SCRS) — College Course Registration System

A friendly, full-stack Spring Boot app for student registration, OTP verification, course browsing, and director-level course management.

---

## Quick Start (Windows)

1. Build:

```bash
mvnw.cmd clean package
```

2. Run:

```bash
mvnw.cmd spring-boot:run
```

3. Open in browser:

- `http://localhost:8080`

---

## What This App Can Do

### Student
- Register with email + mobile number
- Verify email and mobile OTP
- Login and browse courses
- Apply for courses and upload marksheets
- Track application status

### Director (Authority)
- Manage courses (add / edit / delete)
- Define capacity, credits, fee, prerequisites
- View the authority dashboard

---

## User Flow (Beginner Friendly)

1. **Register** ? `/auth/register`
2. **Verify OTPs** ? `/auth/verify-registration`
3. **Login** ? `/auth/login`
4. **Browse Courses** ? `/courses`
5. **Apply for Course** ? `/courses/{id}/enroll`

---

## Demo Authority Accounts (Dev Only)

Enable demo accounts:

```properties
ccrs.dev.create-authority=true
```

Then restart the app. You’ll get:

- Director: `director@college.edu` / `Director123!`
- Admin: `admin@college.edu` / `Admin123!`
- Faculty: `faculty@college.edu` / `Faculty123!`

---

## OTP & SMS Setup

### Email OTP
Configured via `spring.mail.*` in `application.properties`.

### SMS OTP
SMS uses a **pluggable provider**.

Default (mock): writes OTPs to a file for testing:

```properties
ccrs.sms.provider=mock
ccrs.sms.mock-file=logs/sms-otp.log
```

To switch to Twilio later:

```properties
ccrs.sms.provider=twilio
ccrs.otp.send-sms=true
```

And provide credentials (env vars recommended):

```properties
twilio.account-sid=${TWILIO_ACCOUNT_SID:}
twilio.auth-token=${TWILIO_AUTH_TOKEN:}
twilio.from-number=${TWILIO_FROM_NUMBER:}
```

---

## Project Structure

- `config/` — Security and app configuration
- `controller/` — MVC routes and page controllers
- `dto/` — Request/response objects
- `entity/` — JPA entities
- `repository/` — Database repositories
- `security/` — Authentication/authorization logic
- `service/` — Business logic
- `templates/` — Thymeleaf UI pages
- `static/` — CSS / JS / images

---

## Common Issues & Fixes

**Registration 500 error**
- Usually caused by duplicate email/mobile.
- Use a fresh email or delete the old record from DB.

**Delete user fails**
- Remove related rows first (e.g., `user_roles`, `enrollments`).

---

## Build & Run (Linux / Mac)

```bash
./mvnw clean package
./mvnw spring-boot:run
```

---

## Database

Default: MySQL

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ccrs_db
spring.datasource.username=root
spring.datasource.password=your_password
```

---

## Contributing

Feel free to fork and improve. If you’re adding features, please update tests and documentation.

---

## License

MIT (or update this section as needed).
