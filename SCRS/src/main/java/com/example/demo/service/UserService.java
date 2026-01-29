package com.example.demo.service;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.RegistrationResult;
import com.example.demo.entity.OtpVerification;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service that encapsulates user-related operations like registration,
 * lookup and marking email/mobile as verified.
 */
@Service
public class UserService {

    private static final String STUDENT_ROLE = "STUDENT";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    @Value("${ccrs.otp.send-email: true}")
    private boolean sendEmailOtp;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    // Register a new user, persist it and create OTPs for email and mobile verification.
    @Transactional
    public RegistrationResult register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new IllegalArgumentException("Mobile number already registered");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(STUDENT_ROLE);
        user = userRepository.save(user);

        OtpVerification emailOtpRecord = otpService.createAndSendEmailOtp(user.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
        OtpVerification mobileOtpRecord = otpService.createAndSendMobileOtp(user.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);

        String emailOtpForDisplay = sendEmailOtp ? null : emailOtpRecord.getOtp();
        String mobileOtpForDisplay = mobileOtpRecord.getOtp();
        return new RegistrationResult(user, emailOtpForDisplay, mobileOtpForDisplay);
    }

    // Find user by email.
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Find user by mobile number.
    public Optional<User> findByMobileNumber(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }

    // Find by email or mobile (tries email first).
    public Optional<User> findByEmailOrMobile(String emailOrMobile) {
        return userRepository.findByEmail(emailOrMobile)
                .or(() -> userRepository.findByMobileNumber(emailOrMobile));
    }

    // Mark the user's email as verified.
    @Transactional
    public void markEmailVerified(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setEmailVerified(true);
            userRepository.save(u);
        });
    }

    // Mark the user's mobile number as verified.
    @Transactional
    public void markMobileVerified(String mobile) {
        userRepository.findByMobileNumber(mobile).ifPresent(u -> {
            u.setMobileVerified(true);
            userRepository.save(u);
        });
    }

    // Update the user's password (password must already be encoded).
    @Transactional
    public void updatePassword(Long userId, String newPasswordEncoded) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setPassword(newPasswordEncoded);
            userRepository.save(u);
        });
    }
}
