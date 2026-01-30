# CCRS (SCRS)

Simple Spring Boot project for user registration, login, OTP verification, and password reset.

---

## Overview
This application (CCRS) demonstrates typical authentication flows: registration, OTP verification (email and mobile), login, password reset and role-based dashboards. It uses Spring Boot, Spring Security, Thymeleaf and a JPA-backed database.

## New: Role-based login (Student vs College authority)
- The login form now allows selecting the login type (`Student` or `College authority`).
- A custom success handler validates that the selected login type matches the authenticated user's roles and redirects to different dashboards:
  - Student -> `/dashboard`
  - College authority -> `/dashboard/authority`
- For development you can create a demo authority account by setting `ccrs.dev.create-authority=true` in `application.properties` (disabled by default).

---

## Folder structure and rationale
- `src/main/java/com/example/demo`
  - `config/` 🔧 : Application configuration classes (security, dev helpers). Put wiring and beans here.
  - `controller/` 🧭 : MVC controllers that handle HTTP routes and prepare models for Thymeleaf templates.
  - `dto/` 📦 : Simple POJOs used for request/response binding (login, register, reset forms).
  - `entity/` 💾 : JPA entities representing persisted data (User, OtpVerification). Keep domain models here.
  - `repository/` 🗄 : Spring Data JPA repositories for DB access.
  - `security/` 🔐 : Security integrations: `CustomUserDetails`, `UserDetailsService`, and authentication handlers.
  - `service/` 🧩 : Business logic and operations (user registration, OTP handling, etc.).
- `src/main/resources/templates` 🎨 : Thymeleaf views (login, register, dashboard, verification pages).
- `src/main/resources/static` 🖼 : Static assets (CSS/JS/images).

Why this structure?
- Separation of concerns: controllers orchestrate, services implement business rules, repositories handle persistence.
- `security/` keeps security-related code isolated so it's easy to update auth logic without touching business rules.
- `dto/` avoids leaking entity details into the web layer and keeps validation annotations on request payloads.

---

## How to create a college authority (dev)
1. Open `src/main/resources/application.properties` and set `ccrs.dev.create-authority=true`.
2. Restart the app.
3. Demo authority accounts will be created for development/testing (disabled by default):
   - Director: `director@college.edu` / `Director123!` (role: `AUTHORITY_DIRECTOR`)
   - Admin staff: `admin@college.edu` / `Admin123!` (role: `AUTHORITY_ADMIN`)
   - Faculty: `faculty@college.edu` / `Faculty123!` (role: `AUTHORITY_FACULTY`)

Note: These are development/test accounts. Do not enable `ccrs.dev.create-authority` in production.

---

## GitHub / push instructions
I cannot push changes directly to your repository on your behalf without credentials. To push these changes to `https://github.com/kathan7104/CCRS` (please confirm the correct GitHub username and repository URL), run:

1. git add -A
2. git commit -m "Add role-based login (Student / Authority), custom success handler, docs and dev helper"
3. git push origin main

If you want me to perform the push, please provide the repository URL and grant me access (for example, add a deploy key or invite a bot account). I will not ask for your password or token.

---

## Next steps / suggestions
- Add more granular authority roles (e.g., ROLE_ADMIN, ROLE_REGISTRAR) if you need finer access control.
- Protect authority-only routes using role-based `@PreAuthorize` or security matchers.
- Remove or protect developer helper code (`DataInitializer`) before deploying to production.

If you'd like: I can (1) add explicit unit and integration tests for the login flows, (2) add more detailed per-line comments to all files, or (3) prepare a PR-ready branch and detailed commit history for you to review.


## Build and run (Windows)

1. Build:

```bash
mvnw.cmd clean package
```

2. Run:

```bash
mvnw.cmd spring-boot:run
```

Or run the generated jar:

```bash
java -jar target/*.jar
```

## GitHub push (one-time setup)

Replace `main` with your branch name if different.

```bash
# set your user
git config user.name "kathan7104"

# initialize repo if not already
git init

# add remote (only if not already added)
git remote add origin https://github.com/kathan7104/CCRS.git

# stage changes
git add .

# commit
git commit -m "Add project and comments"

# push to GitHub (force if you need to overwrite remote main)
git branch -M main
git push -u origin main
```

If you have two-factor auth, use a personal access token instead of a password when prompted.

## What I changed

- Added this README.md
- Added simple English comments to Java sources (explain file purpose and main methods)

If you want, I can also create a small `CONTRIBUTING.md` or make the commit and push for you if you provide a GitHub token.