# SCRS - Student Course Registration System

Spring Boot web application for student admissions/enrollment, role-based authority workflows, fee setup, invoice generation, and reporting.

## Current System Scope

### Student
- Register account with email + mobile.
- Verify email OTP and mobile OTP.
- Login (email or mobile).
- Browse courses and apply with required details/documents.
- Track application status from dashboard.

### Authority Roles
- `AUTHORITY_ADMIN`
- `AUTHORITY_DIRECTOR`
- `AUTHORITY_STAFF`
- `AUTHORITY_FACULTY` (assignment target role)

### Admin (`/admin`)
- Approve/reject pending enrollment applications.
- Auto-generate invoice on approval.
- Manage authority users (admin/director/staff).
- Manage departments (create/deactivate).
- View unpaid/revenue/reconciliation reports.

### Director (`/director`)
- Department dashboard.
- Manage faculty and student records (with scope checks).
- Manage faculty-course assignments.
- Manage course catalog (`/director/courses`) including:
  - capacity, credits, fee, level, duration, qualification
  - prerequisites
  - teaching schema PDF upload/versioning

### Staff (`/staff`)
- Manage fee structures (CRUD).
- Fee structure audit log tracking.
- Financial and unpaid/reconciliation reports.

## Tech Stack
- Java 21
- Spring Boot 4.0.2
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL (default), H2 (dev profile)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Quick Start

### 1) Prerequisites
- JDK 21+
- MySQL 8+ (for default profile)
- Git

### 2) Configure environment
Update `src/main/resources/application.properties` for your machine:
- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.mail.*` (if real email OTP is enabled)

Recommended: move secrets to environment variables before pushing to GitHub.

### 3) Build and run (Windows)
```bash
mvnw.cmd clean package
mvnw.cmd spring-boot:run
```

### 4) Build and run (Linux/Mac)
```bash
./mvnw clean package
./mvnw spring-boot:run
```

Default URL: `http://localhost:8081`

## Dev Profile (H2)
Run without MySQL:
```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

With H2 profile:
- In-memory DB is used.
- H2 console: `http://localhost:8081/h2-console`

## Demo Accounts
When `ccrs.dev.create-authority=true`, startup seeds:
- `director@college.edu` / `Director123!`
- `admin@college.edu` / `Admin123!`
- `faculty@college.edu` / `Faculty123!`
- `staff@college.edu` / `Staff123!`

Student accounts are created through `/auth/register`.

## Authentication and OTP
- Login page: `/auth/login`
- Supports Student and Authority login mode selection.
- Email OTP controlled by `ccrs.otp.send-email`.
- SMS OTP controlled by `ccrs.otp.send-sms` and `ccrs.sms.provider` (`mock` or `twilio`).
- Mock SMS output file: `logs/sms-otp.log`.

## Important Runtime Behavior
- `DataInitializer` currently resets enrollments and courses on startup and seeds sample courses.
- Upload folders used by app:
  - `uploads/documents/` (enrollment docs)
  - `uploads/teaching-schemas/` (teaching schema PDFs)

If you need persistent production-like data, disable/reset this initializer logic first.

## Main Routes
- Public auth: `/auth/**`
- Student: `/dashboard`, `/courses/**`
- Admin: `/admin/**`
- Director: `/director/**`, `/director/courses/**`
- Staff: `/staff/**`

## Project Structure
- `src/main/java/com/example/demo/config` - security/app bootstrap
- `src/main/java/com/example/demo/controller` - MVC controllers
- `src/main/java/com/example/demo/service` - business services
- `src/main/java/com/example/demo/entity` - JPA entities
- `src/main/java/com/example/demo/repository` - repositories
- `src/main/resources/templates` - Thymeleaf templates
- `src/main/resources/db/schema.sql` - schema bootstrap SQL

## Notes
- Payment entities and reports exist; full payment gateway UI/flow is not yet completed.
- If OTP fails on a new machine, check SMTP/Twilio credentials and the OTP toggles in `application.properties`.
