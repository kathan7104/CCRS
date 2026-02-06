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


/**
 * Web MVC controller for Auth endpoints.
 */
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
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("loginRequest", new LoginRequest());
        if (error != null) {
            model.addAttribute("error", "Invalid email/mobile or password.");
        }
        return "auth/login";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model,
                           HttpServletRequest httpRequest) {
        if (bindingResult.hasErrors()) {
            return "auth/register";
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            return "auth/register";
        }
        try {
            RegistrationResult result = userService.register(request);
            User user = result.getUser();
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("pendingVerificationEmail", user.getEmail());
            session.setAttribute("pendingVerificationMobile", user.getMobileNumber());
            redirectAttributes.addFlashAttribute("success", "Registration successful. Verify email and mobile below.");
            if (result.getEmailOtpForDisplay() != null) {
                redirectAttributes.addFlashAttribute("devOtpEmail", result.getEmailOtpForDisplay());
            }
            if (result.getMobileOtpForDisplay() != null) {
                redirectAttributes.addFlashAttribute("devOtpMobile", result.getMobileOtpForDisplay());
            }
            return "redirect:/auth/verify-registration";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("registerRequest", request);
            model.addAttribute("error", "Email or mobile number already registered");
            return "auth/register";
        } catch (IllegalArgumentException e) {
            model.addAttribute("registerRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        } catch (Exception e) {
            model.addAttribute("registerRequest", request);
            model.addAttribute("error", "Registration failed. Please try again.");
            return "auth/register";
        }
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/verify-registration")
    public String verifyRegistrationPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        String email = session != null ? (String) session.getAttribute("pendingVerificationEmail") : null;
        String mobile = session != null ? (String) session.getAttribute("pendingVerificationMobile") : null;
        if (email == null && mobile == null) {
            return "redirect:/auth/register";
        }
        Optional<User> userOpt = (email != null && !email.isBlank())
                ? userService.findByEmail(email)
                : Optional.empty();
        if (userOpt.isEmpty() && mobile != null && !mobile.isBlank()) {
            userOpt = userService.findByMobileNumber(mobile);
        }
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (u.isEmailVerified() && u.isMobileVerified()) {
                if (session != null) session.invalidate();
                redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
                return "redirect:/auth/login";
            }
        }
        model.addAttribute("email", email != null ? email : "");
        model.addAttribute("mobile", mobile != null ? mobile : "");
        model.addAttribute("emailOtp", "");
        model.addAttribute("mobileOtp", "");
        return "auth/verify-registration";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/verify-email-otp")
    public String verifyEmailOtp(@RequestParam String email, @RequestParam String otp,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Optional<OtpVerification> verified = otpService.verifyOtp(email, otp, OtpVerification.OtpType.EMAIL_VERIFICATION);
        if (verified.isEmpty()) {
            redirectAttributes.addFlashAttribute("emailError", "Invalid or expired OTP.");
        } else {
            userService.markEmailVerified(email);
            redirectAttributes.addFlashAttribute("emailSuccess", "Email verified.");
        }
        return "redirect:/auth/verify-registration";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/verify-mobile-otp")
    public String verifyMobileOtp(@RequestParam String mobile, @RequestParam String otp,
                                  RedirectAttributes redirectAttributes) {
        Optional<OtpVerification> verified = otpService.verifyOtp(mobile, otp, OtpVerification.OtpType.MOBILE_VERIFICATION);
        if (verified.isEmpty()) {
            redirectAttributes.addFlashAttribute("mobileError", "Invalid or expired OTP.");
        } else {
            userService.markMobileVerified(mobile);
            redirectAttributes.addFlashAttribute("mobileSuccess", "Mobile verified.");
        }
        return "redirect:/auth/verify-registration";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/resend-otp")
    public String resendOtp(@RequestParam String type, @RequestParam String identifier,
                           RedirectAttributes redirectAttributes, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/auth/register";
        }
        if ("EMAIL".equalsIgnoreCase(type)) {
            OtpVerification record = otpService.createAndSendEmailOtp(identifier, OtpVerification.OtpType.EMAIL_VERIFICATION);
            redirectAttributes.addFlashAttribute("resendEmailSuccess", "New OTP sent to your email.");
            if (!sendEmailOtp) {
                redirectAttributes.addFlashAttribute("devOtpEmail", record.getOtp());
            }
        } else if ("MOBILE".equalsIgnoreCase(type)) {
            OtpVerification record = otpService.createAndSendMobileOtp(identifier, OtpVerification.OtpType.MOBILE_VERIFICATION);
            redirectAttributes.addFlashAttribute("resendMobileSuccess", "New OTP sent to your mobile.");
            redirectAttributes.addFlashAttribute("devOtpMobile", record.getOtp());
        }
        return "redirect:/auth/verify-registration";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/verify-otp")
    public String verifyOtpPage(@RequestParam(required = false) String type,
                                @RequestParam(required = false) String identifier,
                                Model model) {
        VerifyOtpRequest req = new VerifyOtpRequest();
        req.setOtpType(type != null ? type : "EMAIL");
        req.setIdentifier(identifier != null ? identifier : "");
        model.addAttribute("verifyOtpRequest", req);
        model.addAttribute("identifier", identifier != null ? identifier : "");
        model.addAttribute("otpType", type != null ? type : "EMAIL");
        return "auth/verify-otp";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @ModelAttribute("verifyOtpRequest") VerifyOtpRequest request,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("otpType", request.getOtpType());
            return "auth/verify-otp";
        }
        OtpVerification.OtpType type = mapOtpType(request.getOtpType());
        if (type == null) {
            model.addAttribute("error", "Invalid verification type.");
            model.addAttribute("verifyOtpRequest", request);
            model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("otpType", request.getOtpType());
            return "auth/verify-otp";
        }
        Optional<OtpVerification> verified = otpService.verifyOtp(request.getIdentifier(), request.getOtp(), type);
        if (verified.isEmpty()) {
            model.addAttribute("verifyOtpRequest", request);
            model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("otpType", request.getOtpType());
            model.addAttribute("error", "Invalid or expired OTP.");
            return "auth/verify-otp";
        }
        if (type == OtpVerification.OtpType.EMAIL_VERIFICATION) {
            userService.markEmailVerified(request.getIdentifier());
            redirectAttributes.addFlashAttribute("success", "Email verified. Now verify your mobile.");
            String mobile = userService.findByEmail(request.getIdentifier()).map(User::getMobileNumber).orElse("");
            return "redirect:/auth/verify-otp?type=MOBILE&identifier=" + mobile;
        }
        if (type == OtpVerification.OtpType.MOBILE_VERIFICATION) {
            userService.markMobileVerified(request.getIdentifier());
        }
        redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
        return "redirect:/auth/login";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage(Model model) {
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        return "auth/forgot-password";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/forgot-password")
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/forgot-password";
        }
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            model.addAttribute("forgotPasswordRequest", request);
            model.addAttribute("error", "No account found with this email.");
            return "auth/forgot-password";
        }
        User user = userOpt.get();
        otpService.createForgotPasswordOtp(user.getEmail(), user.getId());
        redirectAttributes.addFlashAttribute("success", "OTP sent to your email. Enter it below to reset password.");
        redirectAttributes.addFlashAttribute("email", user.getEmail());
        return "redirect:/auth/reset-password?email=" + user.getEmail();
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String email, Model model) {
        ResetPasswordRequest req = new ResetPasswordRequest();
        req.setEmail(email != null ? email : "");
        model.addAttribute("resetPasswordRequest", req);
        model.addAttribute("email", email);
        return "auth/reset-password";
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/reset-password")
    public String resetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("email", request.getEmail());
            return "auth/reset-password";
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            model.addAttribute("email", request.getEmail());
            return "auth/reset-password";
        }
        Optional<OtpVerification> verified = otpService.verifyOtp(
                request.getEmail(), request.getOtp(), OtpVerification.OtpType.FORGOT_PASSWORD);
        if (verified.isEmpty()) {
            model.addAttribute("resetPasswordRequest", request);
            model.addAttribute("email", request.getEmail());
            model.addAttribute("error", "Invalid or expired OTP.");
            return "auth/reset-password";
        }
        Long userId = verified.get().getUserId();
        userService.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
        redirectAttributes.addFlashAttribute("success", "Password reset successfully. Please login.");
        return "redirect:/auth/login";
    }
    private static OtpVerification.OtpType mapOtpType(String value) {
        if (value == null) return null;
        return switch (value.toUpperCase()) {
            case "EMAIL" -> OtpVerification.OtpType.EMAIL_VERIFICATION;
            case "MOBILE" -> OtpVerification.OtpType.MOBILE_VERIFICATION;
            case "FORGOT_PASSWORD" -> OtpVerification.OtpType.FORGOT_PASSWORD;
            default -> null;
        };
    }
/**
 * Web MVC controller for Auth endpoints.
 */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/auth/login?logout";
    }
}
