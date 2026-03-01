# CCRS - College Course Registration System

## Flow of System

```text
User -> Login/Register -> OTP Verification -> Role-based Dashboard

STUDENT FLOW
Browse Courses -> Apply with Required Documents -> Admin Review
-> APPROVED -> Semester Invoice -> Payment -> ENROLLED

AUTHORITY FLOW
Admin    -> Enrollment approvals, authority users, departments, reports
Director -> Courses, teaching schemas, subject extraction, assignments
Staff    -> Fee structures, invoice generation, payment collection
Faculty  -> Assigned subject roster and enrolled student view
```

## Current System Scope

- End-to-end course registration lifecycle for students
- Role-based authority operations (`ADMIN`, `DIRECTOR`, `STAFF`, `FACULTY`)
- OTP-backed account verification and password recovery
- Department/program/course/subject management with teaching schema ingestion
- Financial workflow: fee structures, invoices, online/offline payments, reconciliation

## Detailed Functional Requirements

### 1. Authentication and Account

- User can register as student with full name, email, mobile, and password.
- System sends email OTP and mobile OTP during registration verification flow.
- OTP validity is 10 minutes and OTP records are marked used after successful verification.
- Forgot-password flow uses OTP-based verification before password reset.
- Login type is explicit (`STUDENT` or `AUTHORITY`) and mismatched role logins are blocked.
- After successful login, user is redirected by role to role-specific dashboard.

### 2. Student Module

- Student can browse all courses and open course details.
- Student can apply to one active course at a time (multiple active applications are blocked).
- System validates prerequisites and seat availability before accepting application.
- System captures student personal/academic details in application form.
- Student uploads required documents with validation:
  - Allowed file types: `pdf`, `jpg`, `jpeg`, `png`
  - Per-file size limit: 20 MB
- System enforces program-level mandatory docs:
  - UG: SSC + HSC + School Leaving
  - PG: Bachelor marksheets + Degree certificate
- Course-level required document types are additionally enforced.
- Application initially moves to `PENDING` status.

### 3. Admin Module (Specific)

- Admin dashboard shows:
  - Pending enrollment approvals count
  - Managed authority user count (Admin/Director/Staff)
  - Financial snapshot
- Admin can view pending enrollments and perform workflow actions:
  - Approve enrollment -> status becomes `APPROVED`
  - Reject enrollment -> status becomes `CANCELLED` and seat is released back to course
- Admin can manage authority users with roles:
  - `AUTHORITY_ADMIN`
  - `AUTHORITY_DIRECTOR`
  - `AUTHORITY_STAFF`
- Admin user management includes create, edit, and delete.
- Admin can manage departments:
  - List departments
  - Add new department (duplicate-safe)
  - Deactivate department
- Admin reports include:
  - Revenue/unpaid snapshot
  - Unpaid student report
  - Payment reconciliation report

### 4. Director Module

- Director dashboard shows departmental operational counters.
- Director can create/update/delete courses.
- Course creation supports:
  - Program and batch-year driven code generation
  - Capacity and remaining-seat normalization
  - Required document type mapping
- Director can attach teaching schema by:
  - Uploading `pdf/doc/docx`
  - Selecting existing schema linked to department/program
- On schema upload/link, subjects are auto-extracted and upserted.
- Director can manage department users (students/faculty) with role boundaries.
- Director can activate/deactivate managed users.
- Director can assign one or many subjects to faculty with duplicate prevention.
- Director can remove faculty-subject assignments.
- System syncs subjects from schema files, including fallback sync from uploads folder.

### 5. Staff Module

- Staff manages fee structures (create/update/delete).
- Only one fee structure remains active at a time (others auto-deactivate).
- Every fee-structure change is audit-logged.
- Staff can generate semester invoices for active students.
- Staff can collect offline payments (`CASH`/`CHEQUE`) against invoices.
- Invoice status transitions are handled (`DUE`, `PARTIAL`, `PAID`).
- Staff reports include unpaid list, reconciliation, and financial snapshot.

### 6. Faculty Module

- Faculty can open assigned roster page.
- Roster shows assigned subjects and mapped approved/enrolled students by program.
- Faculty view is department-aware and filtered to faculty assignments.

### 7. Payment Module

- Student payment dashboard shows semester dues and invoice history.
- Checkout supports provider strategy:
  - Mock gateway (default)
  - Razorpay (when enabled/configured)
- Mock flow supports simulated UPI/card validation and transaction capture.
- Razorpay flow verifies order/payment/signature before marking success.
- First semester payment completion can auto-convert `APPROVED` enrollments to `ENROLLED`.

### 8. Startup and Data Compatibility

- Startup initializer patches legacy schema/table gaps for compatibility.
- Startup can seed departments and optional demo data via feature flags.
- Startup backfills subjects from existing teaching schema records/files.

## Tech Stack

- Java 21
- Spring Boot `4.0.2`
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA + Hibernate
- MySQL (default profile)
- H2 (development profile)
- Maven Wrapper
- Apache PDFBox + Apache POI

## Quick Start

1. Clone and open:

```bash
git clone https://github.com/kathan7104/CCRS.git
cd CCRS/SCRS
```

2. Configure runtime values:

- Use environment variables from `.env.example`
- Keep secrets out of git-tracked files
- Local machine overrides can be kept in `src/main/resources/application-local.properties`

3. Run with MySQL profile:

```bash
./mvnw spring-boot:run
```

Windows:

```bash
.\mvnw.cmd spring-boot:run
```

4. Run with H2 profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=h2
```

Windows:

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

5. Open:

- App: `http://localhost:8082`
- H2 console: `http://localhost:8082/h2-console`

## Demo Accounts

Default config keeps authority seeding disabled.

Enable for demo users:

- `ccrs.dev.create-authority=true`
- `ccrs.dev.seed-demo-faculty=true` (optional)

Seeded users:

- Director: `director@college.edu` / `Director123!`
- Admin: `admin@college.edu` / `Admin123!`
- Staff: `staff@college.edu` / `Staff123!`
- Faculty: `faculty@college.edu` / `Faculty123!`

## Authentication and OTP

- OTP types: `EMAIL_VERIFICATION`, `MOBILE_VERIFICATION`, `FORGOT_PASSWORD`
- Email OTP uses SMTP through `JavaMailSender`
- SMS OTP uses provider abstraction (`mock` or `twilio`)
- OTP expiry and used-state are enforced at verification time

## Important Runtime Behavior

- Upload limits are configured as unlimited at server level; per-file checks are enforced in controller/service logic.
- Upload paths used by application:
  - `uploads/documents/`
  - `uploads/teaching-schemas/`
- Payment provider default is mock (`ccrs.payment.provider=mock`).
- Course approval does not immediately mark enrollment as `ENROLLED`; enrollment transitions to `ENROLLED` after first-semester payment.

## Main Routes

### Auth/Public

- `GET /`
- `GET/POST /auth/login`
- `GET/POST /auth/register`
- `GET /auth/verify-registration`
- `POST /auth/verify-email-otp`
- `POST /auth/verify-mobile-otp`
- `POST /auth/resend-otp`
- `GET/POST /auth/forgot-password`
- `GET/POST /auth/reset-password`
- `POST /auth/logout`

### Student

- `GET /dashboard`
- `GET /courses`
- `GET /courses/{id}`
- `GET/POST /courses/{id}/enroll`
- `GET /payments`
- `GET/POST /payments/{invoiceId}/checkout`
- `POST /payments/{invoiceId}/mock/complete`
- `POST /payments/{invoiceId}/razorpay/verify`

### Admin

- `GET /admin/dashboard`
- `GET /admin/enrollments`
- `POST /admin/enrollments/{id}/approve`
- `POST /admin/enrollments/{id}/reject`
- `GET /admin/users`
- `GET /admin/users/new`
- `POST /admin/users`
- `GET /admin/users/{id}/edit`
- `POST /admin/users/{id}`
- `POST /admin/users/{id}/delete`
- `GET /admin/reports`
- `GET /admin/departments`
- `POST /admin/departments`
- `POST /admin/departments/{id}/deactivate`

### Director

- `GET /director/dashboard`
- `GET /director/users`
- `GET /director/users/new`
- `POST /director/users`
- `GET /director/users/{id}/edit`
- `POST /director/users/{id}`
- `POST /director/users/{id}/activate`
- `POST /director/users/{id}/deactivate`
- `GET /director/courses`
- `GET /director/courses/new`
- `POST /director/courses`
- `GET /director/courses/{id}/edit`
- `POST /director/courses/{id}`
- `POST /director/courses/{id}/delete`
- `GET /director/assignments`
- `POST /director/assignments`
- `POST /director/assignments/{id}/delete`

### Staff

- `GET /staff/dashboard`
- `GET /staff/fee-structures`
- `GET /staff/fee-structures/new`
- `POST /staff/fee-structures`
- `GET /staff/fee-structures/{id}/edit`
- `POST /staff/fee-structures/{id}`
- `POST /staff/fee-structures/{id}/delete`
- `GET /staff/invoices`
- `POST /staff/invoices/generate`
- `POST /staff/invoices/{invoiceId}/offline-payment`
- `GET /staff/reports`

### Faculty

- `GET /faculty/roster`

## Project Structure

```text
src/main/java/com/example/demo
  config/
  controller/
  dto/
  entity/
  repository/
  security/
  service/

src/main/resources
  templates/
  static/
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

- Keep credentials in environment variables for DB/SMTP/Twilio/Razorpay.
- Avoid committing local dumps/files with personal data.
- For production, disable mock integrations and set real providers.

## APIs That Are Used

### External Integrations

- SMTP for email OTP (`spring-boot-starter-mail`)
- Twilio API for SMS OTP (`https://api.twilio.com/2010-04-01/Accounts/{SID}/Messages.json`)
- Razorpay API:
  - `POST /v1/orders`
  - `GET /v1/payments/{paymentId}`

### Internal APIs

- MVC route groups for auth, student, admin, director, staff, and faculty modules

## Service Development Concepts Used

### Spring Boot + Layered Architecture

- Controller -> Service -> Repository separation
- Dependency Injection and transaction boundaries for consistency

### Thymeleaf + Server-Side Rendering

- Form binding, validation feedback, and role-based view behavior

### Hibernate / JPA

- Entity relationship mapping for users, courses, enrollments, invoices, payments
- Repository-driven querying and schema update strategy

### OTP and Notification Services

- Secure OTP generation and persistence
- Async email dispatch
- Pluggable SMS provider strategy (mock/twilio)

### Payment Service Concepts

- Strategy-based payment provider selection
- Signature verification for Razorpay callbacks
- Controlled invoice/payment state transitions

### Document and Teaching Schema Ingestion

- Multi-format extraction (`pdf`, `doc`, `docx`)
- Subject parsing and idempotent upsert workflow
- Startup/runtime backfill support

# deployed link
- https://college-course-registration-system.onrender.com/auth/login?logout=&type=student
