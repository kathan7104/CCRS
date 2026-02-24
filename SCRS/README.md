# CCRS - College Course Registration System

## Flow of System

```text
Student/Auth User -> Login/Register -> OTP Verification -> Role-based Dashboard

STUDENT FLOW
Browse Courses -> Enroll with Documents -> Admin Approval -> Invoice Generation
-> Online/Offline Payment -> Enrollment moves to ENROLLED

AUTHORITY FLOW
Admin: user + enrollment approvals + reports
Director: course + teaching schema + subject/faculty assignment
Staff: fee structure + invoice generation + payment recording
Faculty: assigned subject roster
```

## Current System Scope

- Student self-registration with email + mobile and OTP verification
- Role-based login and dashboards for `STUDENT`, `AUTHORITY_ADMIN`, `AUTHORITY_DIRECTOR`, `AUTHORITY_STAFF`, `AUTHORITY_FACULTY`
- Course browsing and enrollment application flow with document uploads
- Enrollment lifecycle: pending -> approved/rejected -> enrolled
- Director-managed teaching schema uploads (`.pdf`, `.doc`, `.docx`) and automatic subject extraction
- Faculty subject assignment and roster views
- Fee structure management and semester invoice generation
- Payment flow with mock provider and optional Razorpay integration
- Admin/staff reporting for unpaid students, revenue, and reconciliation

## Tech Stack

- Java 21
- Spring Boot `4.0.2`
- Spring MVC + Thymeleaf (SSR rendering)
- Spring Security (form login, role authorization, custom filters/handlers)
- Spring Data JPA + Hibernate
- MySQL (default runtime profile)
- H2 (in-memory development profile)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Apache PDFBox + Apache POI (teaching schema parsing)

## Quick Start

1. Clone and open:

```bash
git clone https://github.com/kathan7104/CCRS.git
cd CCRS/SCRS
```

2. Configure runtime values:

- Preferred: set environment variables from `.env.example`
- Local profile file exists at `src/main/resources/application-local.properties`
- Keep secrets in env vars for GitHub safety

3. Run (default profile, MySQL):

```bash
./mvnw spring-boot:run
```

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

4. Run with H2 profile (no MySQL):

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Windows:

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

5. Open:

- App: `http://localhost:8082`
- H2 Console (h2 profile): `http://localhost:8082/h2-console`

## Demo Accounts

By default (`application.properties`) authority seeding is disabled:

- `ccrs.dev.create-authority=false`
- `ccrs.dev.seed-demo-faculty=false`

To auto-create demo authority users at startup, enable:

- `ccrs.dev.create-authority=true`
- (optional) `ccrs.dev.seed-demo-faculty=true`

Seeded accounts (from `DataInitializer`):

- Director: `director@college.edu` / `Director123!`
- Admin: `admin@college.edu` / `Admin123!`
- Staff: `staff@college.edu` / `Staff123!`
- Faculty: `faculty@college.edu` / `Faculty123!` (when `ccrs.dev.seed-demo-faculty=true`)

Student accounts are usually self-registered through `/auth/register`.

## Authentication and OTP

- Login page supports explicit login type: `STUDENT` or `AUTHORITY`
- `PreLoginRoleValidationFilter` blocks mismatched login-type attempts before auth processing
- `CustomAuthenticationSuccessHandler` redirects by role:
  - Student -> `/dashboard`
  - Admin -> `/admin/dashboard`
  - Director -> `/director/dashboard`
  - Staff -> `/staff/dashboard`
  - Faculty -> `/faculty/roster`
- OTP service supports:
  - Email OTP (`ccrs.otp.send-email=true/false`)
  - Mobile OTP (`ccrs.otp.send-sms=true/false`)
  - Forgot-password OTP
- OTP validity: 10 minutes, stored in `otp_verifications` table

## Important Runtime Behavior

- Startup compatibility patching (`DataInitializer`): creates/patches required tables/columns for legacy schemas
- Startup subject backfill: re-processes stored teaching schema files and upserts subjects
- Upload behavior:
  - Multipart and Tomcat upload limits are configured as unlimited in `UploadConfig`
  - Runtime upload directories are used under `uploads/`
- Payment behavior:
  - `ccrs.payment.provider=mock` by default
  - Razorpay flow available when configured and enabled
- Enrollment status automation:
  - On successful semester-1 payment, approved enrollments can move to `ENROLLED`

## Main Routes

- Public/Auth
  - `GET /`
  - `/auth/login`, `/auth/register`, `/auth/verify-registration`
  - `/auth/forgot-password`, `/auth/reset-password`, `/auth/logout`
- Student
  - `GET /dashboard`
  - `/courses`, `/courses/{id}`, `/courses/{id}/enroll`
  - `/payments`, `/payments/{invoiceId}/checkout`
- Admin
  - `/admin/dashboard`
  - `/admin/enrollments`
  - `/admin/users`
  - `/admin/reports`
  - `/admin/departments`
- Director
  - `/director/dashboard`
  - `/director/users`
  - `/director/courses`
  - `/director/assignments`
- Staff
  - `/staff/dashboard`
  - `/staff/fee-structures`
  - `/staff/invoices`
  - `/staff/reports`
- Faculty
  - `/faculty/roster`

## Project Structure

```text
src/main/java/com/example/demo
  config/       # boot/security/upload/startup config
  controller/   # MVC controllers and route handling
  dto/          # request/response transfer objects
  entity/       # JPA entities
  repository/   # Spring Data repositories
  security/     # custom auth filter + success handler + user details
  service/      # business logic services

src/main/resources
  templates/    # Thymeleaf views
  static/       # css/static assets
  application.properties
  application-local.properties
  application-h2.properties
  db/schema.sql

uploads/
  documents/
  teaching-schemas/
  marksheets/
```

## Notes

- Keep `application-local.properties` local; do not commit secrets
- Use environment variables for DB, mail, Twilio, and Razorpay credentials
- `ccrs_db.sql` / `ccrs_db_nodb.sql` are local database dumps and may include test data
- For production, disable mock OTP/payment providers and lock down credentials

## API's That is used

### External APIs/Protocols

- SMTP (Gmail or compatible): via Spring Mail (`JavaMailSender`) for email OTP
- Twilio REST API: `POST https://api.twilio.com/2010-04-01/Accounts/{SID}/Messages.json`
- Razorpay REST API:
  - `POST /v1/orders` (create order)
  - `GET /v1/payments/{paymentId}` (verify payment status)

### Internal Application APIs (MVC endpoints)

- Auth API routes under `/auth/**`
- Course and enrollment routes under `/courses/**`
- Payment routes under `/payments/**`
- Role-based admin/director/staff/faculty route groups

## Service Development Concepts Used

### Spring Boot Core

- Convention-over-configuration app bootstrapping
- Profile-based runtime (`default`, `local`, `h2`)
- Dependency injection across controller -> service -> repository layers

### Thymeleaf (Server-Side Rendering)

- HTML templates in `src/main/resources/templates`
- MVC model binding for forms and validation messages
- Role-based UI navigation using Spring Security integration

### Service Layer Pattern

- Controllers stay thin; business rules are centralized in services
- Transaction boundaries are applied for financial workflows (`@Transactional`)
- Clear domain services:
  - `OtpService`
  - `EnrollmentService`
  - `StudentPaymentService`
  - `StaffBillingService`
  - `TeachingSchemaSubjectIngestionService`
  - `ReportingService`

### Hibernate + JPA

- Entity-first domain modeling (`User`, `Course`, `Enrollment`, `Invoice`, `Payment`, etc.)
- Repository abstraction via Spring Data JPA interfaces
- Schema evolution with `spring.jpa.hibernate.ddl-auto=update`

### OTP Service Design

- Generates secure random 6-digit OTP
- Persists OTP records with type + expiry + used status
- Supports separate flows for registration verification and password reset
- Allows dev-mode behavior (log/show OTP) when external providers are disabled

### SMTP Service Usage

- Email sending via `JavaMailSender`
- Async OTP dispatch using `@Async`
- Graceful fallback: log warning on SMTP failure, avoid app crash

### SMS Provider Strategy (Mock/Twilio)

- `SmsSender` abstraction with pluggable provider implementations
- `MockSmsSender` for local/dev testing
- `TwilioSmsSender` for production-like delivery when credentials are present

### Payment Service Concepts

- Provider strategy (`mock` vs `razorpay`) using config
- Razorpay signature verification using HMAC-SHA256
- Invoice/payment reconciliation and status transitions (`DUE`, `PARTIAL`, `PAID`)

### Document Ingestion Concepts

- Multi-format parsing (`.pdf`, `.doc`, `.docx`)
- Subject code/name/semester parsing with regex heuristics
- Idempotent upsert by department + subject code

---

If you want, I can also add architecture diagrams (sequence/activity) and screenshots section for GitHub.
