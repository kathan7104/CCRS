/*
 * File: src/test/java/com/example/demo/web/ManualCaseAutomationWebTests.java
 * Role: Test
 * MVC Fit: Automated tests for application behavior.
 * Connects To: Verifies layers in isolation or integration
 */

package com.example.demo.web;

import com.example.demo.TestSupportConfig;
import com.example.demo.entity.Course;
import com.example.demo.entity.Department;
import com.example.demo.entity.OtpVerification;
import com.example.demo.entity.User;
import com.example.demo.repository.CourseRepository;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.OtpVerificationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.FacultyRosterService;
import com.example.demo.service.FeeStructureService;
import com.example.demo.service.ReportingService;
import com.example.demo.service.StaffBillingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(properties = {
        "ccrs.dev.create-authority=false",
        "ccrs.dev.seed-demo-faculty=false",
        "ccrs.otp.send-email=false",
        "ccrs.otp.send-sms=false"
})
// Class Summary: Test class that verifies application behavior.
@AutoConfigureMockMvc
@Import({TestSupportConfig.class, ManualCaseAutomationWebTests.MockServicesConfig.class})
class ManualCaseAutomationWebTests {

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores mockMvc for this class.
    private MockMvc mockMvc;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores userRepository for this class.
    private UserRepository userRepository;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores otpVerificationRepository for this class.
    private OtpVerificationRepository otpVerificationRepository;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores courseRepository for this class.
    private CourseRepository courseRepository;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores departmentRepository for this class.
    private DepartmentRepository departmentRepository;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores passwordEncoder for this class.
    private PasswordEncoder passwordEncoder;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores reportingService for this class.
    private ReportingService reportingService;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores facultyRosterService for this class.
    private FacultyRosterService facultyRosterService;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores feeStructureService for this class.
    private FeeStructureService feeStructureService;

// @Autowired asks Spring to inject this dependency.
    @Autowired
// Field: stores staffBillingService for this class.
    private StaffBillingService staffBillingService;

    @Test
    void manualAuth01RegistrationAndOtpVerification() throws Exception {
        String email = "student" + System.nanoTime() + "@example.com";
        String mobile = "9" + String.format("%09d", Math.abs(System.nanoTime()) % 1_000_000_000);
        String password = "TestPass1";

        mockMvc.perform(post("/auth/register")
                        .with(csrf())
                        .param("fullName", "Student Demo")
                        .param("email", email)
                        .param("mobileNumber", mobile)
                        .param("password", password)
                        .param("confirmPassword", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/verify-registration"));

        OtpVerification emailOtp = otpVerificationRepository
                .findTopByIdentifierAndOtpTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, OtpVerification.OtpType.EMAIL_VERIFICATION, LocalDateTime.now())
                .orElseThrow();

        OtpVerification mobileOtp = otpVerificationRepository
                .findTopByIdentifierAndOtpTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        mobile, OtpVerification.OtpType.MOBILE_VERIFICATION, LocalDateTime.now())
                .orElseThrow();

        mockMvc.perform(post("/auth/verify-email-otp")
                        .with(csrf())
                        .param("email", email)
                        .param("otp", emailOtp.getOtp()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/verify-registration"));

        mockMvc.perform(post("/auth/verify-mobile-otp")
                        .with(csrf())
                        .param("mobile", mobile)
                        .param("otp", mobileOtp.getOtp()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/verify-registration"));

        User saved = userRepository.findByEmail(email).orElseThrow();
        assertThat(saved.isEmailVerified()).isTrue();
        assertThat(saved.isMobileVerified()).isTrue();
    }

    @Test
    void manualAuth04ForgotPasswordFlow() throws Exception {
        String email = "reset" + System.nanoTime() + "@example.com";
        String mobile = "9" + String.format("%09d", Math.abs(System.nanoTime()) % 1_000_000_000);
        User user = new User(email, mobile, passwordEncoder.encode("OldPass1"), "Reset User");
        userRepository.save(user);

        mockMvc.perform(post("/auth/forgot-password")
                        .with(csrf())
                        .param("email", email))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/reset-password?email=" + email));

        OtpVerification otp = otpVerificationRepository
                .findTopByIdentifierAndOtpTypeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, OtpVerification.OtpType.FORGOT_PASSWORD, LocalDateTime.now())
                .orElseThrow();

        String newPassword = "NewPass1";
        mockMvc.perform(post("/auth/reset-password")
                        .with(csrf())
                        .param("email", email)
                        .param("otp", otp.getOtp())
                        .param("newPassword", newPassword)
                        .param("confirmPassword", newPassword))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));

        User updated = userRepository.findByEmail(email).orElseThrow();
        assertThat(passwordEncoder.matches(newPassword, updated.getPassword())).isTrue();
    }

    @Test
    void manualStu01CoursesListLoads() throws Exception {
        Course course = sampleCourse("CSE-101");
        courseRepository.save(course);
        CustomUserDetails student = buildUser("student@demo.edu", "STUDENT", "Student Demo", "Computer Applications");

        mockMvc.perform(get("/courses").with(user(student)))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/list"))
                .andExpect(model().attributeExists("courses"));
    }

    @Test
    void manualStu02CourseDetailLoads() throws Exception {
        Course course = sampleCourse("CSE-102");
        courseRepository.save(course);
        CustomUserDetails student = buildUser("student2@demo.edu", "STUDENT", "Student Demo", "Computer Applications");

        mockMvc.perform(get("/courses/" + course.getId()).with(user(student)))
                .andExpect(status().isOk())
                .andExpect(view().name("courses/detail"))
                .andExpect(model().attributeExists("course"));
    }

    @Test
    void manualAdm04DuplicateDepartmentBlocked() throws Exception {
        String name = "Engineering";
        if (departmentRepository.findByNameIgnoreCase(name).isEmpty()) {
            Department department = new Department();
            department.setName(name);
            department.setActive(true);
            departmentRepository.save(department);
        }

        CustomUserDetails admin = buildUser("admin@demo.edu", "AUTHORITY_ADMIN", "Admin Demo", "Central");

        mockMvc.perform(post("/admin/departments").with(user(admin)).with(csrf())
                        .param("name", name))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/departments"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    void manualAdm05ReportsRender() throws Exception {
        when(reportingService.getFinancialSnapshot())
                .thenReturn(new ReportingService.FinancialSnapshot(BigDecimal.ZERO, 0));
        when(reportingService.getUnpaidStudentsReport()).thenReturn(List.of());
        when(reportingService.getReconciliationReport()).thenReturn(List.of());

        CustomUserDetails admin = buildUser("admin2@demo.edu", "AUTHORITY_ADMIN", "Admin Demo", "Central");

        mockMvc.perform(get("/admin/reports").with(user(admin)))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reports"));
    }

    @Test
    void manualFac01RosterLoads() throws Exception {
        when(facultyRosterService.getFacultyRoster(any())).thenReturn(List.of());

        CustomUserDetails faculty = buildUser("faculty@demo.edu", "AUTHORITY_FACULTY", "Faculty Demo", "Computer Applications");

        mockMvc.perform(get("/faculty/roster").with(user(faculty)))
                .andExpect(status().isOk())
                .andExpect(view().name("faculty/roster"))
                .andExpect(model().attributeExists("subjectRosters"));
    }

// Test method: verifies behavior with assertions and test setup.
    private CustomUserDetails buildUser(String email, String role, String fullName, String department) {
        User user = new User();
        user.setEmail(email);
        user.setMobileNumber("900" + String.format("%07d", Math.abs(email.hashCode()) % 10_000_000));
        user.setPassword("password");
        user.setFullName(fullName);
        user.setDepartment(department);
        user.setActive(true);
        user.getRoles().add(role);
        return new CustomUserDetails(user);
    }

// Test method: verifies behavior with assertions and test setup.
    private Course sampleCourse(String code) {
        Course course = new Course();
        course.setCode(code);
        course.setName("Sample Course " + code);
        course.setDepartment("Computer Applications");
        course.setProgramName("BCA");
        course.setBatchYear(2026);
        course.setCapacity(60);
        course.setRemainingSeats(60);
        course.setCredits(4);
        course.setFee(50000);
        course.setProgramLevel("UG");
        course.setLevel("UG");
        course.setDurationSemesters(6);
        course.setRequiredQualification("12th pass");
        return course;
    }

    @TestConfiguration
    static class MockServicesConfig {
// @Bean tells Spring to manage the returned object as a bean.
        @Bean
        ReportingService reportingService() {
            return mock(ReportingService.class);
        }

// @Bean tells Spring to manage the returned object as a bean.
        @Bean
        FacultyRosterService facultyRosterService() {
            return mock(FacultyRosterService.class);
        }

// @Bean tells Spring to manage the returned object as a bean.
        @Bean
        FeeStructureService feeStructureService() {
            return mock(FeeStructureService.class);
        }

// @Bean tells Spring to manage the returned object as a bean.
        @Bean
        StaffBillingService staffBillingService() {
            return mock(StaffBillingService.class);
        }
    }
}
