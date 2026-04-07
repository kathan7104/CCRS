# Test Report - CCRS (College Course Registration System)

**Project:** SCRS (CCRS)
**Report Date:** 2026-03-31
**Test Type:** Manual + Automation (JUnit/Spring Boot)
**Environment:** Local (Java 21, Spring Boot 4.0.2, H2 in-memory for tests)
**Build/Commit:** N/A (not captured)
**Automation Command:** `./mvnw -q test`
**Test Profile:** default

## Summary

| Metric | Count |
|---|---:|
| Total Manual Test Cases | 25 |
| Total Automated Tests | 17 |
| Total Test Cases | 42 |
| Passed | 24 |
| Failed | 0 |
| Blocked | 0 |
| Not Executed | 18 |

**Note:** Manual tests are documented but not executed in this run. Automation results are from `target/surefire-reports`.

## Test Cases (Tabular)

| ID | Module | Scenario | Steps (High-Level) | Expected Result | Status | Evidence |
|---|---|---|---|---|---|---|
| AUTH-01 | Auth | Student registration with email + mobile OTP | Open /auth/register -> fill form -> submit -> verify email OTP -> verify mobile OTP | Account created, both verifications succeed, login enabled | Passed (Automated) | AUTO-manualAuth01RegistrationAndOtpVerification |
| AUTH-02 | Auth | OTP expiry enforcement | Request OTP -> wait past expiry -> submit OTP | OTP rejected as expired; prompt to resend | Not Executed | N/A |
| AUTH-03 | Auth | Login role mismatch blocked | Open /auth/login?type=authority -> login using student account | Login blocked with wrong-role message | Not Executed | N/A |
| AUTH-04 | Auth | Forgot password with OTP reset | Open /auth/forgot-password -> request OTP -> reset password | Password updated; login succeeds with new password | Passed (Automated) | AUTO-manualAuth04ForgotPasswordFlow |
| STU-01 | Student | Browse courses list | Login as student -> open /courses | Courses list renders with available courses | Passed (Automated) | AUTO-manualStu01CoursesListLoads |
| STU-02 | Student | Course detail view | Open /courses/{id} | Course details page shows name, fee, credits, seats | Passed (Automated) | AUTO-manualStu02CourseDetailLoads |
| STU-03 | Student | Apply with missing mandatory docs | Open enroll -> submit without required docs | Validation error; application not submitted | Not Executed | N/A |
| STU-04 | Student | Document type validation | Upload unsupported file type (e.g., .exe) during enrollment | Upload rejected with file type error | Not Executed | N/A |
| STU-05 | Student | Enforce single active application | Submit application -> attempt second application | Second application blocked with message | Not Executed | N/A |
| ADM-01 | Admin | Approve enrollment | Login as admin -> open pending enrollment -> approve | Status becomes APPROVED; seat consumed | Not Executed | N/A |
| ADM-02 | Admin | Reject enrollment releases seat | Reject pending enrollment | Status becomes CANCELLED; seat restored | Not Executed | N/A |
| ADM-03 | Admin | Manage authority users | Create -> edit -> deactivate authority user | User record updates reflected in list and status | Not Executed | N/A |
| ADM-04 | Admin | Department duplicate prevention | Create department with existing name | Duplicate prevented or merged; no duplicate record | Passed (Automated) | AUTO-manualAdm04DuplicateDepartmentBlocked |
| ADM-05 | Admin | Reports render | Open /admin/reports | Revenue and unpaid reports load without error | Passed (Automated) | AUTO-manualAdm05ReportsRender |
| DIR-01 | Director | Create course with capacity | Create new course with capacity/fee/credits | Course saved; remaining seats initialized | Not Executed | N/A |
| DIR-02 | Director | Attach teaching schema (upload) | Upload teaching schema docx for a program | Subjects extracted and upserted for program | Not Executed | N/A |
| DIR-03 | Director | Assign subjects to faculty | Assign subject -> attempt duplicate assignment | Duplicate assignment blocked | Not Executed | N/A |
| DIR-04 | Director | Activate/deactivate users | Toggle user active status | User status updates and reflects in login access | Not Executed | N/A |
| STF-01 | Staff | Fee structure activation | Create new fee structure and activate | Previous structure deactivated automatically | Not Executed | N/A |
| STF-02 | Staff | Invoice generation | Generate invoices for enrolled students | Invoices created with correct totals | Not Executed | N/A |
| STF-03 | Staff | Offline payment capture | Record CASH/CHEQUE payment for invoice | Invoice status updates to PAID/PARTIAL | Not Executed | N/A |
| PAY-01 | Payment | Mock checkout success | Pay via mock gateway | Payment success; invoice updated | Not Executed | N/A |
| PAY-02 | Payment | Razorpay verification | Verify payment signature | Payment marked SUCCESS | Not Executed | N/A |
| FAC-01 | Faculty | Roster view | Login as faculty -> open /faculty/roster | Assigned subjects and students listed | Passed (Automated) | AUTO-manualFac01RosterLoads |
| SYS-01 | Startup | Backfill subjects from schema | Start app with existing teaching schemas | Subjects backfilled for all schemas | Not Executed | N/A |

## Automated Test Results

| ID | Framework | Test Class | Test Name | Result | Duration | Evidence |
|---|---|---|---|---|---|---|
| AUTO-contextLoads | JUnit | com.example.demo.ScrsApplicationTests | contextLoads | Passed | 0.280 s | Surefire XML |
| AUTO-forgotPasswordPageLoads | JUnit | com.example.demo.web.AuthControllerWebTests | forgotPasswordPageLoads | Passed | 0.011 s | Surefire XML |
| AUTO-loginPageAuthorityLoads | JUnit | com.example.demo.web.AuthControllerWebTests | loginPageAuthorityLoads | Passed | 0.009 s | Surefire XML |
| AUTO-loginPageDefaultsToStudent | JUnit | com.example.demo.web.AuthControllerWebTests | loginPageDefaultsToStudent | Passed | 0.023 s | Surefire XML |
| AUTO-loginPageShowsRegistrationClosedError | JUnit | com.example.demo.web.AuthControllerWebTests | loginPageShowsRegistrationClosedError | Passed | 0.010 s | Surefire XML |
| AUTO-loginPageShowsWrongRoleError | JUnit | com.example.demo.web.AuthControllerWebTests | loginPageShowsWrongRoleError | Passed | 0.010 s | Surefire XML |
| AUTO-loginPageStudentLoads | JUnit | com.example.demo.web.AuthControllerWebTests | loginPageStudentLoads | Passed | 0.011 s | Surefire XML |
| AUTO-registerPageLoads | JUnit | com.example.demo.web.AuthControllerWebTests | registerPageLoads | Passed | 0.372 s | Surefire XML |
| AUTO-verifyOtpPageDefaults | JUnit | com.example.demo.web.AuthControllerWebTests | verifyOtpPageDefaults | Passed | 0.009 s | Surefire XML |
| AUTO-verifyOtpPageHonorsParams | JUnit | com.example.demo.web.AuthControllerWebTests | verifyOtpPageHonorsParams | Passed | 0.015 s | Surefire XML |
| AUTO-manualAdm04DuplicateDepartmentBlocked | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualAdm04DuplicateDepartmentBlocked | Passed | 0.046 s | Surefire XML |
| AUTO-manualAdm05ReportsRender | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualAdm05ReportsRender | Passed | 0.197 s | Surefire XML |
| AUTO-manualAuth01RegistrationAndOtpVerification | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualAuth01RegistrationAndOtpVerification | Passed | 0.143 s | Surefire XML |
| AUTO-manualAuth04ForgotPasswordFlow | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualAuth04ForgotPasswordFlow | Passed | 0.703 s | Surefire XML |
| AUTO-manualFac01RosterLoads | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualFac01RosterLoads | Passed | 0.025 s | Surefire XML |
| AUTO-manualStu01CoursesListLoads | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualStu01CoursesListLoads | Passed | 0.085 s | Surefire XML |
| AUTO-manualStu02CourseDetailLoads | JUnit | com.example.demo.web.ManualCaseAutomationWebTests | manualStu02CourseDetailLoads | Passed | 0.035 s | Surefire XML |

## Test Data / Preconditions

- Use seeded demo accounts if enabled in config.
- Ensure at least one department, course, and fee structure exist for end-to-end flows.
- Mock payment provider by default (`ccrs.payment.provider=mock`).
- Automated tests run with an in-memory H2 database.

## Out of Scope

- Load/performance testing
- Security penetration testing
- Browser compatibility beyond modern Chromium

## Risks / Gaps

- Execution evidence (screenshots/logs) not captured.

## Next Execution Notes

- Update `Build/Commit` and environment when running tests.
- Replace `Not Executed` statuses with real outcomes.
- Attach evidence links or filenames per test case.
