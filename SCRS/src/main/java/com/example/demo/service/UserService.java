// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.dto.RegisterRequest;
// Import statement: brings a class into scope by name.
import com.example.demo.dto.RegistrationResult;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import com.example.demo.entity.User;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.UserRepository;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.security.crypto.password.PasswordEncoder;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Service;
// Import statement: brings a class into scope by name.
import org.springframework.transaction.annotation.Transactional;

// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Service that encapsulates user-related operations like registration,
 // Comment: explains code for readers.
 * lookup and marking email/mobile as verified.
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Service
// Class declaration: defines a new type.
public class UserService {

    // Field declaration: defines a member variable.
    private static final String STUDENT_ROLE = "STUDENT";

    // Field declaration: defines a member variable.
    private final UserRepository userRepository;
    // Field declaration: defines a member variable.
    private final PasswordEncoder passwordEncoder;
    // Field declaration: defines a member variable.
    private final OtpService otpService;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.otp.send-email: true}")
    // Field declaration: defines a member variable.
    private boolean sendEmailOtp;

    // Opens a method/constructor/block.
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        // Uses current object (this) to access a field or method.
        this.userRepository = userRepository;
        // Uses current object (this) to access a field or method.
        this.passwordEncoder = passwordEncoder;
        // Uses current object (this) to access a field or method.
        this.otpService = otpService;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Register a new user, persist it and create OTPs for email and mobile verification.
    // Annotation: adds metadata used by frameworks/tools.
    @Transactional
    // Opens a method/constructor/block.
    public RegistrationResult register(RegisterRequest request) {
        // Statement: Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        // Statement: Optional<User> byMobile = userRepository.findByMobileNumber(request.getMobile...
        Optional<User> byMobile = userRepository.findByMobileNumber(request.getMobileNumber());
        // Opens a method/constructor/block.
        if (byEmail.isPresent() || byMobile.isPresent()) {
            // Statement: User existing = byEmail.orElseGet(byMobile::get);
            User existing = byEmail.orElseGet(byMobile::get);
            // Conditional: runs this block only if the condition is true.
            if (byEmail.isPresent() && byMobile.isPresent()
                    // Opens a method/constructor/block.
                    && !byEmail.get().getId().equals(byMobile.get().getId())) {
                // Throw: raises an exception.
                throw new IllegalArgumentException("Email or mobile number already registered");
            // Closes the current code block.
            }
            // Opens a method/constructor/block.
            if (!existing.isEmailVerified() || !existing.isMobileVerified()) {
                // Statement: OtpVerification emailOtpRecord = null;
                OtpVerification emailOtpRecord = null;
                // Statement: OtpVerification mobileOtpRecord = null;
                OtpVerification mobileOtpRecord = null;
                // Opens a method/constructor/block.
                if (!existing.isEmailVerified()) {
                    // Statement: emailOtpRecord = otpService.createAndSendEmailOtp(existing.getEmail(), OtpVer...
                    emailOtpRecord = otpService.createAndSendEmailOtp(existing.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
                // Closes the current code block.
                }
                // Opens a method/constructor/block.
                if (!existing.isMobileVerified()) {
                    // Statement: mobileOtpRecord = otpService.createAndSendMobileOtp(existing.getMobileNumber(...
                    mobileOtpRecord = otpService.createAndSendMobileOtp(existing.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);
                // Closes the current code block.
                }
                // Statement: String emailOtpForDisplay = (emailOtpRecord == null || sendEmailOtp) ? null :...
                String emailOtpForDisplay = (emailOtpRecord == null || sendEmailOtp) ? null : emailOtpRecord.getOtp();
                // Statement: String mobileOtpForDisplay = mobileOtpRecord != null ? mobileOtpRecord.getOtp...
                String mobileOtpForDisplay = mobileOtpRecord != null ? mobileOtpRecord.getOtp() : null;
                // Return: sends a value back to the caller.
                return new RegistrationResult(existing, emailOtpForDisplay, mobileOtpForDisplay);
            // Closes the current code block.
            }
            // Throw: raises an exception.
            throw new IllegalArgumentException("Email or mobile number already registered");
        // Closes the current code block.
        }
        // Statement: User user = new User();
        User user = new User();
        // Statement: user.setFullName(request.getFullName());
        user.setFullName(request.getFullName());
        // Statement: user.setEmail(request.getEmail());
        user.setEmail(request.getEmail());
        // Statement: user.setMobileNumber(request.getMobileNumber());
        user.setMobileNumber(request.getMobileNumber());
        // Statement: user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Statement: user.getRoles().add(STUDENT_ROLE);
        user.getRoles().add(STUDENT_ROLE);
        // Statement: user = userRepository.save(user);
        user = userRepository.save(user);

        // Statement: OtpVerification emailOtpRecord = otpService.createAndSendEmailOtp(user.getEma...
        OtpVerification emailOtpRecord = otpService.createAndSendEmailOtp(user.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
        // Statement: OtpVerification mobileOtpRecord = otpService.createAndSendMobileOtp(user.getM...
        OtpVerification mobileOtpRecord = otpService.createAndSendMobileOtp(user.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);

        // Statement: String emailOtpForDisplay = sendEmailOtp ? null : emailOtpRecord.getOtp();
        String emailOtpForDisplay = sendEmailOtp ? null : emailOtpRecord.getOtp();
        // Statement: String mobileOtpForDisplay = mobileOtpRecord.getOtp();
        String mobileOtpForDisplay = mobileOtpRecord.getOtp();
        // Return: sends a value back to the caller.
        return new RegistrationResult(user, emailOtpForDisplay, mobileOtpForDisplay);
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Find user by email.
    // Opens a method/constructor/block.
    public Optional<User> findByEmail(String email) {
        // Return: sends a value back to the caller.
        return userRepository.findByEmail(email);
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Find user by mobile number.
    // Opens a method/constructor/block.
    public Optional<User> findByMobileNumber(String mobile) {
        // Return: sends a value back to the caller.
        return userRepository.findByMobileNumber(mobile);
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Find by email or mobile (tries email first).
    // Opens a method/constructor/block.
    public Optional<User> findByEmailOrMobile(String emailOrMobile) {
        // Return: sends a value back to the caller.
        return userRepository.findByEmail(emailOrMobile)
                // Statement: .or(() -> userRepository.findByMobileNumber(emailOrMobile));
                .or(() -> userRepository.findByMobileNumber(emailOrMobile));
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Mark the user's email as verified.
    // Annotation: adds metadata used by frameworks/tools.
    @Transactional
    // Opens a method/constructor/block.
    public void markEmailVerified(String email) {
        // Opens a method/constructor/block.
        userRepository.findByEmail(email).ifPresent(u -> {
            // Statement: u.setEmailVerified(true);
            u.setEmailVerified(true);
            // Statement: userRepository.save(u);
            userRepository.save(u);
        // Statement: });
        });
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Mark the user's mobile number as verified.
    // Annotation: adds metadata used by frameworks/tools.
    @Transactional
    // Opens a method/constructor/block.
    public void markMobileVerified(String mobile) {
        // Opens a method/constructor/block.
        userRepository.findByMobileNumber(mobile).ifPresent(u -> {
            // Statement: u.setMobileVerified(true);
            u.setMobileVerified(true);
            // Statement: userRepository.save(u);
            userRepository.save(u);
        // Statement: });
        });
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Update the user's password (password must already be encoded).
    // Annotation: adds metadata used by frameworks/tools.
    @Transactional
    // Opens a method/constructor/block.
    public void updatePassword(Long userId, String newPasswordEncoded) {
        // Opens a method/constructor/block.
        userRepository.findById(userId).ifPresent(u -> {
            // Statement: u.setPassword(newPasswordEncoded);
            u.setPassword(newPasswordEncoded);
            // Statement: userRepository.save(u);
            userRepository.save(u);
        // Statement: });
        });
    // Closes the current code block.
    }
// Closes the current code block.
}
