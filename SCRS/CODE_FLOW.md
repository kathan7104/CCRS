# Code Flow and Line-by-Line Explanation (Key Files)

This document explains the major files and flow in the CCRS project and includes detailed notes to help explain the implementation to others.

---

## 1) `AuthController.java`
- Class level: Handles all web routes under `/auth` (register, login, otp, reset, logout).
- `loginPage(...)` (GET `/login`):
  - Prepares `LoginRequest` model for form binding.
  - Reads optional `type` param to show "STUDENT" or "AUTHORITY" login view.
  - If `m=wrongRole` is present, shows a user-friendly error (wrong role selected).
- Register / verify endpoints:
  - `register` saves a new `User` and creates OTPs for email/mobile.
  - `verify-email-otp` / `verify-mobile-otp` validate OTPs via `OtpService` and mark user as verified.
- Forgot/reset flows:
  - `forgotPassword` finds user by email and sends forget-password OTP.
  - `resetPassword` verifies OTP and updates password using `UserService`.
- `logout` is a POST-only route that uses `SecurityContextLogoutHandler`.

Detailed flow notes:
- Registration creates users with `STUDENT` role by default.
- Authentication is performed by Spring Security based on `CustomUserDetailsService` and `CustomUserDetails`.

---

## 2) `SecurityConfig.java`
- Configures password encoder, `DaoAuthenticationProvider`, and `SecurityFilterChain`.
- Public paths include `/auth/**`, static assets and `/h2-console/**`.
- Form login:
  - `loginPage` is `/auth/login`.
  - `loginProcessingUrl` is `/auth/login` (the POST target).
  - A custom success handler (`CustomAuthenticationSuccessHandler`) is used to redirect to the appropriate dashboard.

Note: The custom success handler validates that the user selected the correct login type (STUDENT/AUTHORITY) and redirects accordingly.

---

## 3) `CustomUserDetailsService.java` & `CustomUserDetails.java`
- `CustomUserDetailsService` looks up a `User` by email and then mobile number if email not found.
- `CustomUserDetails` wraps the `User` and provides authorities in the format `ROLE_<ROLE_NAME>`.
- `getUsername()` returns the user's email (used as the 'username' by Spring Security). Mobile login still works since lookup is handled earlier.

---

## 4) `CustomAuthenticationSuccessHandler.java`
- Validates the `loginType` parameter (posted from the login form).
- Checks the authenticated user's authorities:
  - If `loginType` is `AUTHORITY` and the user doesn't have `ROLE_AUTHORITY`, redirect back with an error.
  - If check passes, redirect to `/dashboard/authority` for authority users; student users go to `/dashboard`.

---

## 5) `UserService.java` and `User.java`
- `UserService.register(...)` populates a `User` entity, hashes password and assigns the `STUDENT` role.
- `User` stores roles as an `ElementCollection` of strings and has verification flags for email/mobile.

---

## 6) `DataInitializer.java` (dev helper)
- When `ccrs.dev.create-authority` is `true` in `application.properties`, this creates a demo authority account (`authority@college.edu`) for development/testing.
- The demo account is deliberately disabled by default (config = false) to avoid accidental creation in production.

---

## Where to add further comments
- The project contains concise JavaDoc comments on each class and method already. For a complete line-by-line annotation, expand the notes above by file and insert a comment before each significant operation.

If you would like, I can extend this `CODE_FLOW.md` into per-file, per-line commentary (very verbose). Tell me which files should be annotated first (for example: `AuthController` and `SecurityConfig`).
