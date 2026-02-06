package com.example.demo.controller;
import com.example.demo.dto.*;
import com.example.demo.entity.OtpVerification;
import com.example.demo.entity.User;
import com.example.demo.service.OtpService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    @Value("${ccrs.otp.send-email: true}")
    private boolean sendEmailOtp;
    public AuthController(UserService userService, OtpService otpService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        // 1. Put data on the page so the user can see it
        model.addAttribute("loginRequest", new LoginRequest());
        // 2. Check a rule -> decide what to do next
        if (error != null) {
            // 3. Put data on the page so the user can see it
            model.addAttribute("error", "Invalid email/mobile or password.");
        }
        // 4. Send the result back to the screen
        return "auth/login";
    }
    @GetMapping("/register")
    public String registerPage(Model model) {
        // 1. Put data on the page so the user can see it
        model.addAttribute("registerRequest", new RegisterRequest());
        // 2. Send the result back to the screen
        return "auth/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpServletRequest httpRequest) {
        // 1. Check a rule -> decide what to do next
        if (bindingResult.hasErrors()) {
            // 2. Send the result back to the screen
            return "auth/register";
        }
        // 3. Check a rule -> decide what to do next
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            // 4. Add a message so the user knows what went wrong
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            // 5. Send the result back to the screen
            return "auth/register";
        }
        try {
            // 6. Ask the service to do the main work
            RegistrationResult result = userService.register(request);
            User user = result.getUser();
            // 7. Find the user session so we can remember them
            HttpSession session = httpRequest.getSession(true);
            // 8. Save something in the session for later
            session.setAttribute("pendingVerificationEmail", user.getEmail());
            // 9. Save something in the session for later
            session.setAttribute("pendingVerificationMobile", user.getMobileNumber());
            // 10. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("success", "Registration successful. Verify email and mobile below.");
            // 11. Check a rule -> decide what to do next
            if (result.getEmailOtpForDisplay() != null) {
                // 12. Show a one-time message on the next page
                redirectAttributes.addFlashAttribute("devOtpEmail", result.getEmailOtpForDisplay());
            }
            // 13. Check a rule -> decide what to do next
            if (result.getMobileOtpForDisplay() != null) {
                // 14. Show a one-time message on the next page
                redirectAttributes.addFlashAttribute("devOtpMobile", result.getMobileOtpForDisplay());
            }
            // 15. Send the result back to the screen
            return "redirect:/auth/verify-registration";
        } catch (DataIntegrityViolationException e) {
            // 16. Put data on the page so the user can see it
            model.addAttribute("registerRequest", request);
            // 17. Put data on the page so the user can see it
            model.addAttribute("error", "Email or mobile number already registered");
            // 18. Send the result back to the screen
            return "auth/register";
        } catch (IllegalArgumentException e) {
            // 19. Put data on the page so the user can see it
            model.addAttribute("registerRequest", request);
            // 20. Put data on the page so the user can see it
            model.addAttribute("error", e.getMessage());
            // 21. Send the result back to the screen
            return "auth/register";
        } catch (Exception e) {
            // 22. Put data on the page so the user can see it
            model.addAttribute("registerRequest", request);
            // 23. Put data on the page so the user can see it
            model.addAttribute("error", "Registration failed. Please try again.");
            // 24. Send the result back to the screen
            return "auth/register";
        }
    }
    @GetMapping("/verify-registration")
    public String verifyRegistrationPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        // 1. Find the user session so we can remember them
        HttpSession session = request.getSession(false);
        // 2. Read saved session data
        String email = session != null ? (String) session.getAttribute("pendingVerificationEmail") : null;
        // 3. Read saved session data
        String mobile = session != null ? (String) session.getAttribute("pendingVerificationMobile") : null;
        // 4. Check a rule -> decide what to do next
        if (email == null && mobile == null) {
            // 5. Send the result back to the screen
            return "redirect:/auth/register";
        }
        Optional<User> userOpt = (email != null && !email.isBlank())
                // 6. Ask the service to do the main work
                ? userService.findByEmail(email)
                : Optional.empty();
        // 7. Check a rule -> decide what to do next
        if (userOpt.isEmpty() && mobile != null && !mobile.isBlank()) {
            // 8. Ask the service to do the main work
            userOpt = userService.findByMobileNumber(mobile);
        }
        // 9. Check a rule -> decide what to do next
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            // 10. Check a rule -> decide what to do next
            if (u.isEmailVerified() && u.isMobileVerified()) {
                // 11. Check a rule -> decide what to do next
                if (session != null) session.invalidate();
                // 12. Show a one-time message on the next page
                redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
                // 13. Send the result back to the screen
                return "redirect:/auth/login";
            }
        }
        // 14. Put data on the page so the user can see it
        model.addAttribute("email", email != null ? email : "");
        // 15. Put data on the page so the user can see it
        model.addAttribute("mobile", mobile != null ? mobile : "");
        // 16. Put data on the page so the user can see it
        model.addAttribute("emailOtp", "");
        // 17. Put data on the page so the user can see it
        model.addAttribute("mobileOtp", "");
        // 18. Send the result back to the screen
        return "auth/verify-registration";
    }
    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(@RequestParam String email, @RequestParam String otp,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // 1. Ask the service to do the main work
        Optional<OtpVerification> verified = otpService.verifyOtp(email, otp, OtpVerification.OtpType.EMAIL_VERIFICATION);
        // 2. Check a rule -> decide what to do next
        if (verified.isEmpty()) {
            // 3. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("emailError", "Invalid or expired OTP.");
        } else {
            // 4. Ask the service to do the main work
            userService.markEmailVerified(email);
            // 5. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("emailSuccess", "Email verified.");
        }
        // 6. Send the result back to the screen
        return "redirect:/auth/verify-registration";
    }
    @PostMapping("/verify-mobile-otp")
    public String verifyMobileOtp(@RequestParam String mobile, @RequestParam String otp,
                                  RedirectAttributes redirectAttributes) {
        // 1. Ask the service to do the main work
        Optional<OtpVerification> verified = otpService.verifyOtp(mobile, otp, OtpVerification.OtpType.MOBILE_VERIFICATION);
        // 2. Check a rule -> decide what to do next
        if (verified.isEmpty()) {
            // 3. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("mobileError", "Invalid or expired OTP.");
        } else {
            // 4. Ask the service to do the main work
            userService.markMobileVerified(mobile);
            // 5. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("mobileSuccess", "Mobile verified.");
        }
        // 6. Send the result back to the screen
        return "redirect:/auth/verify-registration";
    }
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String type, @RequestParam String identifier,
                           RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // 1. Find the user session so we can remember them
        HttpSession session = request.getSession(false);
        // 2. Check a rule -> decide what to do next
        if (session == null) {
            // 3. Send the result back to the screen
            return "redirect:/auth/register";
        }
        // 4. Check a rule -> decide what to do next
        if ("EMAIL".equalsIgnoreCase(type)) {
            // 5. Ask the service to do the main work
            OtpVerification record = otpService.createAndSendEmailOtp(identifier, OtpVerification.OtpType.EMAIL_VERIFICATION);
            // 6. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("resendEmailSuccess", "New OTP sent to your email.");
            // 7. Check a rule -> decide what to do next
            if (!sendEmailOtp) {
                // 8. Show a one-time message on the next page
                redirectAttributes.addFlashAttribute("devOtpEmail", record.getOtp());
            }
        } else if ("MOBILE".equalsIgnoreCase(type)) {
            // 9. Ask the service to do the main work
            OtpVerification record = otpService.createAndSendMobileOtp(identifier, OtpVerification.OtpType.MOBILE_VERIFICATION);
            // 10. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("resendMobileSuccess", "New OTP sent to your mobile.");
            // 11. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("devOtpMobile", record.getOtp());
        }
        // 12. Send the result back to the screen
        return "redirect:/auth/verify-registration";
    }
    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String type,
                                @RequestParam(required = false) String identifier,
                                Model model) {
        VerifyOtpRequest req = new VerifyOtpRequest();
        req.setOtpType(type != null ? type : "EMAIL");
        req.setIdentifier(identifier != null ? identifier : "");
        // 1. Put data on the page so the user can see it
        model.addAttribute("verifyOtpRequest", req);
        // 2. Put data on the page so the user can see it
        model.addAttribute("identifier", identifier != null ? identifier : "");
        // 3. Put data on the page so the user can see it
        model.addAttribute("otpType", type != null ? type : "EMAIL");
        // 4. Send the result back to the screen
        return "auth/verify-otp";
    }
    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @ModelAttribute("verifyOtpRequest") VerifyOtpRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        // 1. Check a rule -> decide what to do next
        if (bindingResult.hasErrors()) {
            // 2. Put data on the page so the user can see it
            model.addAttribute("identifier", request.getIdentifier());
            // 3. Put data on the page so the user can see it
            model.addAttribute("otpType", request.getOtpType());
            // 4. Send the result back to the screen
            return "auth/verify-otp";
        }
        OtpVerification.OtpType type = mapOtpType(request.getOtpType());
        // 5. Check a rule -> decide what to do next
        if (type == null) {
            // 6. Put data on the page so the user can see it
            model.addAttribute("error", "Invalid verification type.");
            // 7. Put data on the page so the user can see it
            model.addAttribute("verifyOtpRequest", request);
            // 8. Put data on the page so the user can see it
            model.addAttribute("identifier", request.getIdentifier());
            // 9. Put data on the page so the user can see it
            model.addAttribute("otpType", request.getOtpType());
            // 10. Send the result back to the screen
            return "auth/verify-otp";
        }
        // 11. Ask the service to do the main work
        Optional<OtpVerification> verified = otpService.verifyOtp(request.getIdentifier(), request.getOtp(), type);
        // 12. Check a rule -> decide what to do next
        if (verified.isEmpty()) {
            // 13. Put data on the page so the user can see it
            model.addAttribute("verifyOtpRequest", request);
            // 14. Put data on the page so the user can see it
            model.addAttribute("identifier", request.getIdentifier());
            // 15. Put data on the page so the user can see it
            model.addAttribute("otpType", request.getOtpType());
            // 16. Put data on the page so the user can see it
            model.addAttribute("error", "Invalid or expired OTP.");
            // 17. Send the result back to the screen
            return "auth/verify-otp";
        }
        // 18. Check a rule -> decide what to do next
        if (type == OtpVerification.OtpType.EMAIL_VERIFICATION) {
            // 19. Ask the service to do the main work
            userService.markEmailVerified(request.getIdentifier());
            // 20. Show a one-time message on the next page
            redirectAttributes.addFlashAttribute("success", "Email verified. Now verify your mobile.");
            // 21. Ask the service to do the main work
            String mobile = userService.findByEmail(request.getIdentifier()).map(User::getMobileNumber).orElse("");
            // 22. Send the result back to the screen
            return "redirect:/auth/verify-otp?type=MOBILE&identifier=" + mobile;
        }
        // 23. Check a rule -> decide what to do next
        if (type == OtpVerification.OtpType.MOBILE_VERIFICATION) {
            // 24. Ask the service to do the main work
            userService.markMobileVerified(request.getIdentifier());
        }
        // 25. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
        // 26. Send the result back to the screen
        return "redirect:/auth/login";
    }
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        // 1. Put data on the page so the user can see it
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        // 2. Send the result back to the screen
        return "auth/forgot-password";
    }
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        // 1. Check a rule -> decide what to do next
        if (bindingResult.hasErrors()) {
            // 2. Send the result back to the screen
            return "auth/forgot-password";
        }
        // 3. Ask the service to do the main work
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        // 4. Check a rule -> decide what to do next
        if (userOpt.isEmpty()) {
            // 5. Put data on the page so the user can see it
            model.addAttribute("forgotPasswordRequest", request);
            // 6. Put data on the page so the user can see it
            model.addAttribute("error", "No account found with this email.");
            // 7. Send the result back to the screen
            return "auth/forgot-password";
        }
        User user = userOpt.get();
        // 8. Ask the service to do the main work
        otpService.createForgotPasswordOtp(user.getEmail(), user.getId());
        // 9. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("success", "OTP sent to your email. Enter it below to reset password.");
        // 10. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("email", user.getEmail());
        // 11. Send the result back to the screen
        return "redirect:/auth/reset-password?email=" + user.getEmail();
    }
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String email, Model model) {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setEmail(email != null ? email : "");
        // 1. Put data on the page so the user can see it
        model.addAttribute("resetPasswordRequest", req);
        // 2. Put data on the page so the user can see it
        model.addAttribute("email", email);
        // 3. Send the result back to the screen
        return "auth/reset-password";
    }
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        // 1. Check a rule -> decide what to do next
        if (bindingResult.hasErrors()) {
            // 2. Put data on the page so the user can see it
            model.addAttribute("email", request.getEmail());
            // 3. Send the result back to the screen
            return "auth/reset-password";
        }
        // 4. Check a rule -> decide what to do next
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            // 5. Add a message so the user knows what went wrong
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            // 6. Put data on the page so the user can see it
            model.addAttribute("email", request.getEmail());
            // 7. Send the result back to the screen
            return "auth/reset-password";
        }
        // 8. Ask the service to do the main work
        Optional<OtpVerification> verified = otpService.verifyOtp(
                request.getEmail(), request.getOtp(), OtpVerification.OtpType.FORGOT_PASSWORD);
        // 9. Check a rule -> decide what to do next
        if (verified.isEmpty()) {
            // 10. Put data on the page so the user can see it
            model.addAttribute("resetPasswordRequest", request);
            // 11. Put data on the page so the user can see it
            model.addAttribute("email", request.getEmail());
            // 12. Put data on the page so the user can see it
            model.addAttribute("error", "Invalid or expired OTP.");
            // 13. Send the result back to the screen
            return "auth/reset-password";
        }
        Long userId = verified.get().getUserId();
        // 14. Security: hide the password before saving
        userService.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
        // 15. Show a one-time message on the next page
        redirectAttributes.addFlashAttribute("success", "Password reset successfully. Please login.");
        // 16. Send the result back to the screen
        return "redirect:/auth/login";
    }
    private static OtpVerification.OtpType mapOtpType(String value) {
        // 1. Check a rule -> decide what to do next
        if (value == null) return null;
        // 2. Send the result back to the screen
        return switch (value.toUpperCase()) {
            case "EMAIL" -> OtpVerification.OtpType.EMAIL_VERIFICATION;
            case "MOBILE" -> OtpVerification.OtpType.MOBILE_VERIFICATION;
            case "FORGOT_PASSWORD" -> OtpVerification.OtpType.FORGOT_PASSWORD;
            default -> null;
        };
    }
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 1. Check a rule -> decide what to do next
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        // 2. Send the result back to the screen
        return "redirect:/auth/login?logout";
    }
}
