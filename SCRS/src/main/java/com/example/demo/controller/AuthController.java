// Package declaration: groups related classes in a namespace.
package com.example.demo.controller;

// Import statement: brings a class into scope by name.
import com.example.demo.dto.*;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.service.OtpService;
// Import statement: brings a class into scope by name.
import com.example.demo.service.UserService;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletRequest;
// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpSession;
// Import statement: brings a class into scope by name.
import jakarta.validation.Valid;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.Authentication;
// Import statement: brings a class into scope by name.
import org.springframework.security.core.context.SecurityContextHolder;
// Import statement: brings a class into scope by name.
import org.springframework.security.crypto.password.PasswordEncoder;
// Import statement: brings a class into scope by name.
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Controller;
// Import statement: brings a class into scope by name.
import org.springframework.ui.Model;
// Import statement: brings a class into scope by name.
import org.springframework.validation.BindingResult;
// Import statement: brings a class into scope by name.
import org.springframework.web.bind.annotation.*;
// Import statement: brings a class into scope by name.
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
// Import statement: brings a class into scope by name.
import org.springframework.dao.DataIntegrityViolationException;

// Import statement: brings a class into scope by name.
import jakarta.servlet.http.HttpServletResponse;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Controller that handles authentication-related pages and actions.
 // Comment: explains code for readers.
 * Responsibilities include registration, OTP verification, login, logout,
 // Comment: explains code for readers.
 * forgot-password and reset-password flows.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Controller
// Annotation: adds metadata used by frameworks/tools.
@RequestMapping("/auth")
// Class declaration: defines a new type.
public class AuthController {

    // Field declaration: defines a member variable.
    private final UserService userService;
    // Field declaration: defines a member variable.
    private final OtpService otpService;
    // Field declaration: defines a member variable.
    private final PasswordEncoder passwordEncoder;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.otp.send-email: true}")
    // Field declaration: defines a member variable.
    private boolean sendEmailOtp;

    // Opens a method/constructor/block.
    public AuthController(UserService userService, OtpService otpService, PasswordEncoder passwordEncoder) {
        // Uses current object (this) to access a field or method.
        this.userService = userService;
        // Uses current object (this) to access a field or method.
        this.otpService = otpService;
        // Uses current object (this) to access a field or method.
        this.passwordEncoder = passwordEncoder;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show the login page. If `error` is present, display a login error message.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/login")
    // Opens a method/constructor/block.
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        // Statement: model.addAttribute("loginRequest", new LoginRequest());
        model.addAttribute("loginRequest", new LoginRequest());
        // Opens a method/constructor/block.
        if (error != null) {
            // Statement: model.addAttribute("error", "Invalid email/mobile or password.");
            model.addAttribute("error", "Invalid email/mobile or password.");
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return "auth/login";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show the registration page with an empty form model.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/register")
    // Opens a method/constructor/block.
    public String registerPage(Model model) {
        // Statement: model.addAttribute("registerRequest", new RegisterRequest());
        model.addAttribute("registerRequest", new RegisterRequest());
        // Return: sends a value back to the caller.
        return "auth/register";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Handle registration form submission, create user and send OTPs.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/register")
    // Field declaration: defines a member variable.
    public String register(@Valid @ModelAttribute("registerRequest") RegisterRequest request,
                           // Statement: BindingResult bindingResult,
                           BindingResult bindingResult,
                           // Statement: RedirectAttributes redirectAttributes,
                           RedirectAttributes redirectAttributes,
                           // Statement: Model model,
                           Model model,
                           // Opens a new code block.
                           HttpServletRequest httpRequest) {
        // Opens a method/constructor/block.
        if (bindingResult.hasErrors()) {
            // Return: sends a value back to the caller.
            return "auth/register";
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            // Statement: bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwo...
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            // Return: sends a value back to the caller.
            return "auth/register";
        // Closes the current code block.
        }
        // Opens a new code block.
        try {
            // Statement: RegistrationResult result = userService.register(request);
            RegistrationResult result = userService.register(request);
            // Statement: User user = result.getUser();
            User user = result.getUser();
            // Statement: HttpSession session = httpRequest.getSession(true);
            HttpSession session = httpRequest.getSession(true);
            // Statement: session.setAttribute("pendingVerificationEmail", user.getEmail());
            session.setAttribute("pendingVerificationEmail", user.getEmail());
            // Statement: session.setAttribute("pendingVerificationMobile", user.getMobileNumber());
            session.setAttribute("pendingVerificationMobile", user.getMobileNumber());
            // Statement: redirectAttributes.addFlashAttribute("success", "Registration successful. Ver...
            redirectAttributes.addFlashAttribute("success", "Registration successful. Verify email and mobile below.");
            // Opens a method/constructor/block.
            if (result.getEmailOtpForDisplay() != null) {
                // Statement: redirectAttributes.addFlashAttribute("devOtpEmail", result.getEmailOtpForDisp...
                redirectAttributes.addFlashAttribute("devOtpEmail", result.getEmailOtpForDisplay());
            // Closes the current code block.
            }
            // Opens a method/constructor/block.
            if (result.getMobileOtpForDisplay() != null) {
                // Statement: redirectAttributes.addFlashAttribute("devOtpMobile", result.getMobileOtpForDi...
                redirectAttributes.addFlashAttribute("devOtpMobile", result.getMobileOtpForDisplay());
            // Closes the current code block.
            }
            // Return: sends a value back to the caller.
            return "redirect:/auth/verify-registration";
        // Opens a method/constructor/block.
        } catch (DataIntegrityViolationException e) {
            // Statement: model.addAttribute("registerRequest", request);
            model.addAttribute("registerRequest", request);
            // Statement: model.addAttribute("error", "Email or mobile number already registered");
            model.addAttribute("error", "Email or mobile number already registered");
            // Return: sends a value back to the caller.
            return "auth/register";
        // Opens a method/constructor/block.
        } catch (IllegalArgumentException e) {
            // Statement: model.addAttribute("registerRequest", request);
            model.addAttribute("registerRequest", request);
            // Statement: model.addAttribute("error", e.getMessage());
            model.addAttribute("error", e.getMessage());
            // Return: sends a value back to the caller.
            return "auth/register";
        // Opens a method/constructor/block.
        } catch (Exception e) {
            // Statement: model.addAttribute("registerRequest", request);
            model.addAttribute("registerRequest", request);
            // Statement: model.addAttribute("error", "Registration failed. Please try again.");
            model.addAttribute("error", "Registration failed. Please try again.");
            // Return: sends a value back to the caller.
            return "auth/register";
        // Closes the current code block.
        }
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show verification page where user can enter email/mobile OTPs.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/verify-registration")
    // Opens a method/constructor/block.
    public String verifyRegistrationPage(HttpServletRequest request, Model model, RedirectAttributes redirectAttributes) {
        // Statement: HttpSession session = request.getSession(false);
        HttpSession session = request.getSession(false);
        // Statement: String email = session != null ? (String) session.getAttribute("pendingVerifi...
        String email = session != null ? (String) session.getAttribute("pendingVerificationEmail") : null;
        // Statement: String mobile = session != null ? (String) session.getAttribute("pendingVerif...
        String mobile = session != null ? (String) session.getAttribute("pendingVerificationMobile") : null;
        // Opens a method/constructor/block.
        if (email == null && mobile == null) {
            // Return: sends a value back to the caller.
            return "redirect:/auth/register";
        // Closes the current code block.
        }
        // Statement: Optional<User> userOpt = (email != null && !email.isBlank())
        Optional<User> userOpt = (email != null && !email.isBlank())
                // Statement: ? userService.findByEmail(email)
                ? userService.findByEmail(email)
                // Statement: : Optional.empty();
                : Optional.empty();
        // Opens a method/constructor/block.
        if (userOpt.isEmpty() && mobile != null && !mobile.isBlank()) {
            // Statement: userOpt = userService.findByMobileNumber(mobile);
            userOpt = userService.findByMobileNumber(mobile);
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (userOpt.isPresent()) {
            // Statement: User u = userOpt.get();
            User u = userOpt.get();
            // Opens a method/constructor/block.
            if (u.isEmailVerified() && u.isMobileVerified()) {
                // Conditional: runs this block only if the condition is true.
                if (session != null) session.invalidate();
                // Statement: redirectAttributes.addFlashAttribute("success", "Verification complete. You c...
                redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
                // Return: sends a value back to the caller.
                return "redirect:/auth/login";
            // Closes the current code block.
            }
        // Closes the current code block.
        }
        // Statement: model.addAttribute("email", email != null ? email : "");
        model.addAttribute("email", email != null ? email : "");
        // Statement: model.addAttribute("mobile", mobile != null ? mobile : "");
        model.addAttribute("mobile", mobile != null ? mobile : "");
        // Statement: model.addAttribute("emailOtp", "");
        model.addAttribute("emailOtp", "");
        // Statement: model.addAttribute("mobileOtp", "");
        model.addAttribute("mobileOtp", "");
        // Return: sends a value back to the caller.
        return "auth/verify-registration";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Verify the email OTP and mark email verified on success.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/verify-email-otp")
    // Field declaration: defines a member variable.
    public String verifyEmailOtp(@RequestParam String email, @RequestParam String otp,
                                 // Opens a new code block.
                                 RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // Statement: Optional<OtpVerification> verified = otpService.verifyOtp(email, otp, OtpVeri...
        Optional<OtpVerification> verified = otpService.verifyOtp(email, otp, OtpVerification.OtpType.EMAIL_VERIFICATION);
        // Opens a method/constructor/block.
        if (verified.isEmpty()) {
            // Statement: redirectAttributes.addFlashAttribute("emailError", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("emailError", "Invalid or expired OTP.");
        // Opens a new code block.
        } else {
            // Statement: userService.markEmailVerified(email);
            userService.markEmailVerified(email);
            // Statement: redirectAttributes.addFlashAttribute("emailSuccess", "Email verified.");
            redirectAttributes.addFlashAttribute("emailSuccess", "Email verified.");
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return "redirect:/auth/verify-registration";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Verify the mobile OTP and mark mobile verified on success.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/verify-mobile-otp")
    // Field declaration: defines a member variable.
    public String verifyMobileOtp(@RequestParam String mobile, @RequestParam String otp,
                                  // Opens a new code block.
                                  RedirectAttributes redirectAttributes) {
        // Statement: Optional<OtpVerification> verified = otpService.verifyOtp(mobile, otp, OtpVer...
        Optional<OtpVerification> verified = otpService.verifyOtp(mobile, otp, OtpVerification.OtpType.MOBILE_VERIFICATION);
        // Opens a method/constructor/block.
        if (verified.isEmpty()) {
            // Statement: redirectAttributes.addFlashAttribute("mobileError", "Invalid or expired OTP.");
            redirectAttributes.addFlashAttribute("mobileError", "Invalid or expired OTP.");
        // Opens a new code block.
        } else {
            // Statement: userService.markMobileVerified(mobile);
            userService.markMobileVerified(mobile);
            // Statement: redirectAttributes.addFlashAttribute("mobileSuccess", "Mobile verified.");
            redirectAttributes.addFlashAttribute("mobileSuccess", "Mobile verified.");
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return "redirect:/auth/verify-registration";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Resend an OTP for the given type (EMAIL or MOBILE) to the identifier.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/resend-otp")
    // Field declaration: defines a member variable.
    public String resendOtp(@RequestParam String type, @RequestParam String identifier,
                           // Opens a new code block.
                           RedirectAttributes redirectAttributes, HttpServletRequest request) {
        // Statement: HttpSession session = request.getSession(false);
        HttpSession session = request.getSession(false);
        // Opens a method/constructor/block.
        if (session == null) {
            // Return: sends a value back to the caller.
            return "redirect:/auth/register";
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if ("EMAIL".equalsIgnoreCase(type)) {
            // Statement: OtpVerification record = otpService.createAndSendEmailOtp(identifier, OtpVeri...
            OtpVerification record = otpService.createAndSendEmailOtp(identifier, OtpVerification.OtpType.EMAIL_VERIFICATION);
            // Statement: redirectAttributes.addFlashAttribute("resendEmailSuccess", "New OTP sent to y...
            redirectAttributes.addFlashAttribute("resendEmailSuccess", "New OTP sent to your email.");
            // Opens a method/constructor/block.
            if (!sendEmailOtp) {
                // Statement: redirectAttributes.addFlashAttribute("devOtpEmail", record.getOtp());
                redirectAttributes.addFlashAttribute("devOtpEmail", record.getOtp());
            // Closes the current code block.
            }
        // Opens a method/constructor/block.
        } else if ("MOBILE".equalsIgnoreCase(type)) {
            // Statement: OtpVerification record = otpService.createAndSendMobileOtp(identifier, OtpVer...
            OtpVerification record = otpService.createAndSendMobileOtp(identifier, OtpVerification.OtpType.MOBILE_VERIFICATION);
            // Statement: redirectAttributes.addFlashAttribute("resendMobileSuccess", "New OTP sent to ...
            redirectAttributes.addFlashAttribute("resendMobileSuccess", "New OTP sent to your mobile.");
            // Statement: redirectAttributes.addFlashAttribute("devOtpMobile", record.getOtp());
            redirectAttributes.addFlashAttribute("devOtpMobile", record.getOtp());
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return "redirect:/auth/verify-registration";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show a generic OTP verification page for email/mobile/forgot flows.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/verify-otp")
    // Field declaration: defines a member variable.
    public String verifyOtpPage(@RequestParam(required = false) String type,
                                // Annotation: adds metadata used by frameworks/tools.
                                @RequestParam(required = false) String identifier,
                                // Opens a new code block.
                                Model model) {
        // Statement: VerifyOtpRequest req = new VerifyOtpRequest();
        VerifyOtpRequest req = new VerifyOtpRequest();
        // Statement: req.setOtpType(type != null ? type : "EMAIL");
        req.setOtpType(type != null ? type : "EMAIL");
        // Statement: req.setIdentifier(identifier != null ? identifier : "");
        req.setIdentifier(identifier != null ? identifier : "");
        // Statement: model.addAttribute("verifyOtpRequest", req);
        model.addAttribute("verifyOtpRequest", req);
        // Statement: model.addAttribute("identifier", identifier != null ? identifier : "");
        model.addAttribute("identifier", identifier != null ? identifier : "");
        // Statement: model.addAttribute("otpType", type != null ? type : "EMAIL");
        model.addAttribute("otpType", type != null ? type : "EMAIL");
        // Return: sends a value back to the caller.
        return "auth/verify-otp";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Handle OTP submission for various OTP types and take appropriate actions.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/verify-otp")
    // Field declaration: defines a member variable.
    public String verifyOtp(@Valid @ModelAttribute("verifyOtpRequest") VerifyOtpRequest request,
                            // Statement: BindingResult bindingResult,
                            BindingResult bindingResult,
                            // Statement: RedirectAttributes redirectAttributes,
                            RedirectAttributes redirectAttributes,
                            // Opens a new code block.
                            Model model) {
        // Opens a method/constructor/block.
        if (bindingResult.hasErrors()) {
            // Statement: model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("identifier", request.getIdentifier());
            // Statement: model.addAttribute("otpType", request.getOtpType());
            model.addAttribute("otpType", request.getOtpType());
            // Return: sends a value back to the caller.
            return "auth/verify-otp";
        // Closes the current code block.
        }
        // Statement: OtpVerification.OtpType type = mapOtpType(request.getOtpType());
        OtpVerification.OtpType type = mapOtpType(request.getOtpType());
        // Opens a method/constructor/block.
        if (type == null) {
            // Statement: model.addAttribute("error", "Invalid verification type.");
            model.addAttribute("error", "Invalid verification type.");
            // Statement: model.addAttribute("verifyOtpRequest", request);
            model.addAttribute("verifyOtpRequest", request);
            // Statement: model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("identifier", request.getIdentifier());
            // Statement: model.addAttribute("otpType", request.getOtpType());
            model.addAttribute("otpType", request.getOtpType());
            // Return: sends a value back to the caller.
            return "auth/verify-otp";
        // Closes the current code block.
        }
        // Statement: Optional<OtpVerification> verified = otpService.verifyOtp(request.getIdentifi...
        Optional<OtpVerification> verified = otpService.verifyOtp(request.getIdentifier(), request.getOtp(), type);
        // Opens a method/constructor/block.
        if (verified.isEmpty()) {
            // Statement: model.addAttribute("verifyOtpRequest", request);
            model.addAttribute("verifyOtpRequest", request);
            // Statement: model.addAttribute("identifier", request.getIdentifier());
            model.addAttribute("identifier", request.getIdentifier());
            // Statement: model.addAttribute("otpType", request.getOtpType());
            model.addAttribute("otpType", request.getOtpType());
            // Statement: model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("error", "Invalid or expired OTP.");
            // Return: sends a value back to the caller.
            return "auth/verify-otp";
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (type == OtpVerification.OtpType.EMAIL_VERIFICATION) {
            // Statement: userService.markEmailVerified(request.getIdentifier());
            userService.markEmailVerified(request.getIdentifier());
            // Statement: redirectAttributes.addFlashAttribute("success", "Email verified. Now verify y...
            redirectAttributes.addFlashAttribute("success", "Email verified. Now verify your mobile.");
            // Statement: String mobile = userService.findByEmail(request.getIdentifier()).map(User::ge...
            String mobile = userService.findByEmail(request.getIdentifier()).map(User::getMobileNumber).orElse("");
            // Return: sends a value back to the caller.
            return "redirect:/auth/verify-otp?type=MOBILE&identifier=" + mobile;
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (type == OtpVerification.OtpType.MOBILE_VERIFICATION) {
            // Statement: userService.markMobileVerified(request.getIdentifier());
            userService.markMobileVerified(request.getIdentifier());
        // Closes the current code block.
        }
        // Statement: redirectAttributes.addFlashAttribute("success", "Verification complete. You c...
        redirectAttributes.addFlashAttribute("success", "Verification complete. You can now login.");
        // Return: sends a value back to the caller.
        return "redirect:/auth/login";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show form to request a forgot-password OTP.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/forgot-password")
    // Opens a method/constructor/block.
    public String forgotPasswordPage(Model model) {
        // Statement: model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        model.addAttribute("forgotPasswordRequest", new ForgotPasswordRequest());
        // Return: sends a value back to the caller.
        return "auth/forgot-password";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Handle forgot-password request: create and send OTP to the user's email.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/forgot-password")
    // Field declaration: defines a member variable.
    public String forgotPassword(@Valid @ModelAttribute("forgotPasswordRequest") ForgotPasswordRequest request,
                                 // Statement: BindingResult bindingResult,
                                 BindingResult bindingResult,
                                 // Statement: RedirectAttributes redirectAttributes,
                                 RedirectAttributes redirectAttributes,
                                 // Opens a new code block.
                                 Model model) {
        // Opens a method/constructor/block.
        if (bindingResult.hasErrors()) {
            // Return: sends a value back to the caller.
            return "auth/forgot-password";
        // Closes the current code block.
        }
        // Statement: Optional<User> userOpt = userService.findByEmail(request.getEmail());
        Optional<User> userOpt = userService.findByEmail(request.getEmail());
        // Opens a method/constructor/block.
        if (userOpt.isEmpty()) {
            // Statement: model.addAttribute("forgotPasswordRequest", request);
            model.addAttribute("forgotPasswordRequest", request);
            // Statement: model.addAttribute("error", "No account found with this email.");
            model.addAttribute("error", "No account found with this email.");
            // Return: sends a value back to the caller.
            return "auth/forgot-password";
        // Closes the current code block.
        }
        // Statement: User user = userOpt.get();
        User user = userOpt.get();
        // Statement: otpService.createForgotPasswordOtp(user.getEmail(), user.getId());
        otpService.createForgotPasswordOtp(user.getEmail(), user.getId());
        // Statement: redirectAttributes.addFlashAttribute("success", "OTP sent to your email. Ente...
        redirectAttributes.addFlashAttribute("success", "OTP sent to your email. Enter it below to reset password.");
        // Statement: redirectAttributes.addFlashAttribute("email", user.getEmail());
        redirectAttributes.addFlashAttribute("email", user.getEmail());
        // Return: sends a value back to the caller.
        return "redirect:/auth/reset-password?email=" + user.getEmail();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Show reset-password page where user enters OTP and new password.
    // Annotation: adds metadata used by frameworks/tools.
    @GetMapping("/reset-password")
    // Opens a method/constructor/block.
    public String resetPasswordPage(@RequestParam(required = false) String email, Model model) {
        // Statement: ResetPasswordRequest req = new ResetPasswordRequest();
        ResetPasswordRequest req = new ResetPasswordRequest();
        // Statement: req.setEmail(email != null ? email : "");
        req.setEmail(email != null ? email : "");
        // Statement: model.addAttribute("resetPasswordRequest", req);
        model.addAttribute("resetPasswordRequest", req);
        // Statement: model.addAttribute("email", email);
        model.addAttribute("email", email);
        // Return: sends a value back to the caller.
        return "auth/reset-password";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Handle reset-password submission: verify OTP and update password.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/reset-password")
    // Field declaration: defines a member variable.
    public String resetPassword(@Valid @ModelAttribute("resetPasswordRequest") ResetPasswordRequest request,
                                // Statement: BindingResult bindingResult,
                                BindingResult bindingResult,
                                // Statement: RedirectAttributes redirectAttributes,
                                RedirectAttributes redirectAttributes,
                                // Opens a new code block.
                                Model model) {
        // Opens a method/constructor/block.
        if (bindingResult.hasErrors()) {
            // Statement: model.addAttribute("email", request.getEmail());
            model.addAttribute("email", request.getEmail());
            // Return: sends a value back to the caller.
            return "auth/reset-password";
        // Closes the current code block.
        }
        // Opens a method/constructor/block.
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            // Statement: bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwo...
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
            // Statement: model.addAttribute("email", request.getEmail());
            model.addAttribute("email", request.getEmail());
            // Return: sends a value back to the caller.
            return "auth/reset-password";
        // Closes the current code block.
        }
        // Statement: Optional<OtpVerification> verified = otpService.verifyOtp(
        Optional<OtpVerification> verified = otpService.verifyOtp(
                // Statement: request.getEmail(), request.getOtp(), OtpVerification.OtpType.FORGOT_PASSWORD);
                request.getEmail(), request.getOtp(), OtpVerification.OtpType.FORGOT_PASSWORD);
        // Opens a method/constructor/block.
        if (verified.isEmpty()) {
            // Statement: model.addAttribute("resetPasswordRequest", request);
            model.addAttribute("resetPasswordRequest", request);
            // Statement: model.addAttribute("email", request.getEmail());
            model.addAttribute("email", request.getEmail());
            // Statement: model.addAttribute("error", "Invalid or expired OTP.");
            model.addAttribute("error", "Invalid or expired OTP.");
            // Return: sends a value back to the caller.
            return "auth/reset-password";
        // Closes the current code block.
        }
        // Statement: Long userId = verified.get().getUserId();
        Long userId = verified.get().getUserId();
        // Statement: userService.updatePassword(userId, passwordEncoder.encode(request.getNewPassw...
        userService.updatePassword(userId, passwordEncoder.encode(request.getNewPassword()));
        // Statement: redirectAttributes.addFlashAttribute("success", "Password reset successfully....
        redirectAttributes.addFlashAttribute("success", "Password reset successfully. Please login.");
        // Return: sends a value back to the caller.
        return "redirect:/auth/login";
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Map string values to the OtpType enum used by the application.
    // Opens a method/constructor/block.
    private static OtpVerification.OtpType mapOtpType(String value) {
        // Conditional: runs this block only if the condition is true.
        if (value == null) return null;
        // Opens a method/constructor/block.
        return switch (value.toUpperCase()) {
            // Case label inside a switch statement.
            case "EMAIL" -> OtpVerification.OtpType.EMAIL_VERIFICATION;
            // Case label inside a switch statement.
            case "MOBILE" -> OtpVerification.OtpType.MOBILE_VERIFICATION;
            // Case label inside a switch statement.
            case "FORGOT_PASSWORD" -> OtpVerification.OtpType.FORGOT_PASSWORD;
            // Statement: default -> null;
            default -> null;
        // Statement: };
        };
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Log the user out and redirect to the login page.
    // Annotation: adds metadata used by frameworks/tools.
    @PostMapping("/logout")
    // Opens a method/constructor/block.
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Statement: Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Opens a method/constructor/block.
        if (auth != null) {
            // Creates a new object instance.
            new SecurityContextLogoutHandler().logout(request, response, auth);
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return "redirect:/auth/login?logout";
    // Closes the current code block.
    }
// Closes the current code block.
}
