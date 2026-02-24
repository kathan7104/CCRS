# CCRS - College Course Registration System

CCRS is a Spring Boot web application for:
- student registration and enrollment
- OTP-based verification (email/mobile)
- role-based operations for Admin, Director, Staff, and Faculty
- course + teaching schema management
- subject extraction from teaching schema documents
- billing/reporting workflows

This README is written for someone setting up CCRS on a **new machine**.

## Features

### Student
- Register with email + mobile
- Verify OTP
- Login and browse courses
- Apply to courses with document uploads
- Track application status

### Admin (`/admin`)
- Approve/reject enrollments
- Manage authority users
- Manage departments
- Reports (unpaid, revenue, reconciliation)

### Director (`/director`)
- Manage courses and teaching schema documents
- Manage faculty/students
- Assign subjects to faculty
- Auto-sync subjects from teaching schema files

### Staff (`/staff`)
- Manage fee structures
- View billing and financial reports

## Tech Stack
- Java 21
- Spring Boot 4.0.2
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL (default) / H2 (dev profile)
- Maven Wrapper

## Prerequisites

1. JDK 21 or higher
2. Maven is optional (wrapper included)
3. MySQL 8+ (for default profile)
4. Internet access for Maven dependency download

## 1. Clone and Open

```bash
git clone https://github.com/kathan7104/CCRS.git
cd CCRS/SCRS
```

## 2. Mandatory Configuration Changes on Your System

Edit `src/main/resources/application.properties`.

At minimum, change:
1. `spring.datasource.url`
2. `spring.datasource.username`
3. `spring.datasource.password`
4. `spring.mail.username` and `spring.mail.password` (if email OTP is enabled)

If you keep old credentials from another machine, login/OTP/database flow will fail.

For a safer setup, copy `.env.example` values into your environment (local `.env`, system vars, or Render env vars) and avoid hardcoding secrets in repo files.

## 2.1 Deploy With Existing Local Data (Persistent Cloud DB)

To keep your current local data and also store all new live data:

1. Create a cloud MySQL database.
2. Export your local DB:
```bash
mysqldump -u root -p ccrs_db > ccrs_db.sql
```
3. Import into cloud DB:
```bash
mysql -h <HOST> -P <PORT> -u <USER> -p <DB_NAME> < ccrs_db.sql
```
4. In Render, set these env vars:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver`
- `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
5. Do not set `SPRING_PROFILES_ACTIVE=h2` for production persistent DB.

Result: imported existing data remains, and new records are added into the same cloud DB.

## 3. Build and Run

### Windows
```bash
.\mvnw clean package -DskipTests
.\mvnw spring-boot:run
```

### Linux / macOS
```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

Default app URL:
- `http://localhost:8081`

## 4. Alternative Run (H2, no MySQL)

```bash
.\mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

H2 console:
- `http://localhost:8081/h2-console`

## OTP Setup and Why OTP May Not Come After System Change

This is the most common issue when moving project to another laptop/PC.

### Email OTP settings
- `ccrs.otp.send-email=true` means app will send real email OTP via SMTP.
- Requires valid:
  - `spring.mail.host`
  - `spring.mail.port`
  - `spring.mail.username`
  - `spring.mail.password`

If these are invalid/blocked on your new system:
- OTP email will not arrive.

### Mobile OTP settings
- `ccrs.otp.send-sms=true` + Twilio env vars required for real SMS.
- If SMS is disabled, app can use mock mode.

Relevant properties:
- `ccrs.otp.send-sms`
- `ccrs.sms.provider=mock|twilio`
- `ccrs.sms.mock-file=logs/sms-otp.log`

Twilio env vars (for real SMS):
- `TWILIO_ACCOUNT_SID`
- `TWILIO_AUTH_TOKEN`
- `TWILIO_FROM_NUMBER`

### Safe dev mode (recommended for local setup)
Use this when OTP email/SMS is not configured yet:
1. `ccrs.otp.send-email=false`
2. `ccrs.otp.send-sms=false`
3. `ccrs.sms.provider=mock`

Then:
- email OTP will not depend on SMTP
- mobile OTP logs will be in `logs/sms-otp.log`

## Teaching Schema and Subject Extraction

Supported upload file types:
- `.pdf`
- `.doc`
- `.docx`

Expected parser-friendly format:
- semester heading: `SEMESTER 1`
- subject row: `BCOM-S1-101 Financial Accounting I Credits: 4`

When schema is saved/selected:
- subjects are extracted and upserted into `subjects` table
- assignments page reads from `subjects` for faculty mapping

Sample schema documents are available in:
- `sample_docs/`
- `sample_docs/all_course_teaching_schemas/`

## Important Runtime Notes

Startup initializer (`DataInitializer`) performs schema compatibility patches and subject backfill from teaching schemas.

Upload folders:
- `uploads/documents/`
- `uploads/teaching-schemas/`

Do not delete these folders if you want uploaded schema/document history.

## Roles in the System
- `AUTHORITY_ADMIN`
- `AUTHORITY_DIRECTOR`
- `AUTHORITY_STAFF`
- `AUTHORITY_FACULTY`
- `STUDENT`

## Core Routes
- Auth: `/auth/**`
- Student: `/dashboard`, `/courses/**`
- Admin: `/admin/**`
- Director: `/director/**`, `/director/courses/**`, `/director/assignments`
- Staff: `/staff/**`

## Recommended Security Cleanup Before Production

1. Move secrets from `application.properties` to environment variables.
2. Disable dev/mocked OTP modes.
3. Use strong DB credentials and restrict DB network access.
4. Use production mail/SMS providers with monitored credentials.

## Troubleshooting Quick Checklist

If app starts but functionality fails on a new machine:
1. Check MySQL connectivity and credentials.
2. Check OTP toggles (`ccrs.otp.send-email`, `ccrs.otp.send-sms`).
3. Check SMTP/Twilio credentials.
4. Check write permission for `uploads/` folder.
5. Check startup logs for schema sync/backfill messages.
