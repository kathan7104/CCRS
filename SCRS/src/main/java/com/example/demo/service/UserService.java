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
 * Business logic for User.
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
/**
 * Business logic for User.
 */
    @Transactional
    public RegistrationResult register(RegisterRequest request) {
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        Optional<User> byMobile = userRepository.findByMobileNumber(request.getMobileNumber());
        if (byEmail.isPresent() || byMobile.isPresent()) {
            User existing = byEmail.orElseGet(byMobile::get);
            if (byEmail.isPresent() && byMobile.isPresent()
                    && !byEmail.get().getId().equals(byMobile.get().getId())) {
                throw new IllegalArgumentException("Email or mobile number already registered");
            }
            if (!existing.isEmailVerified() || !existing.isMobileVerified()) {
                OtpVerification emailOtpRecord = null;
                OtpVerification mobileOtpRecord = null;
                if (!existing.isEmailVerified()) {
                    emailOtpRecord = otpService.createAndSendEmailOtp(existing.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
                }
                if (!existing.isMobileVerified()) {
                    mobileOtpRecord = otpService.createAndSendMobileOtp(existing.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);
                }
                String emailOtpForDisplay = (emailOtpRecord == null || sendEmailOtp) ? null : emailOtpRecord.getOtp();
                String mobileOtpForDisplay = mobileOtpRecord != null ? mobileOtpRecord.getOtp() : null;
                return new RegistrationResult(existing, emailOtpForDisplay, mobileOtpForDisplay);
            }
            throw new IllegalArgumentException("Email or mobile number already registered");
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
/**
 * Business logic for User.
 */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
/**
 * Business logic for User.
 */
    public Optional<User> findByMobileNumber(String mobile) {
        return userRepository.findByMobileNumber(mobile);
    }
/**
 * Business logic for User.
 */
    public Optional<User> findByEmailOrMobile(String emailOrMobile) {
        return userRepository.findByEmail(emailOrMobile)
                .or(() -> userRepository.findByMobileNumber(emailOrMobile));
    }
/**
 * Business logic for User.
 */
/**
 * Business logic for User.
 */
    @Transactional
    public void markEmailVerified(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setEmailVerified(true);
            userRepository.save(u);
        });
    }
/**
 * Business logic for User.
 */
/**
 * Business logic for User.
 */
    @Transactional
    public void markMobileVerified(String mobile) {
        userRepository.findByMobileNumber(mobile).ifPresent(u -> {
            u.setMobileVerified(true);
            userRepository.save(u);
        });
    }
/**
 * Business logic for User.
 */
/**
 * Business logic for User.
 */
    @Transactional
    public void updatePassword(Long userId, String newPasswordEncoded) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setPassword(newPasswordEncoded);
            userRepository.save(u);
        });
    }
}
