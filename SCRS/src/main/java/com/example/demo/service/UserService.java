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
    @Transactional
    public RegistrationResult register(RegisterRequest request) {
        // 1. Get or save data in the database
        Optional<User> byEmail = userRepository.findByEmail(request.getEmail());
        // 2. Get or save data in the database
        Optional<User> byMobile = userRepository.findByMobileNumber(request.getMobileNumber());
        // 3. Check a rule -> decide what to do next
        if (byEmail.isPresent() || byMobile.isPresent()) {
            User existing = byEmail.orElseGet(byMobile::get);
            // 4. Check a rule -> decide what to do next
            if (byEmail.isPresent() && byMobile.isPresent()
                    && !byEmail.get().getId().equals(byMobile.get().getId())) {
                // 5. Important: stop here and report an error
                throw new IllegalArgumentException("Email or mobile number already registered");
            }
            // 6. Check a rule -> decide what to do next
            if (!existing.isEmailVerified() || !existing.isMobileVerified()) {
                OtpVerification emailOtpRecord = null;
                OtpVerification mobileOtpRecord = null;
                // 7. Check a rule -> decide what to do next
                if (!existing.isEmailVerified()) {
                    // 8. Ask the service to do the main work
                    emailOtpRecord = otpService.createAndSendEmailOtp(existing.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
                }
                // 9. Check a rule -> decide what to do next
                if (!existing.isMobileVerified()) {
                    // 10. Ask the service to do the main work
                    mobileOtpRecord = otpService.createAndSendMobileOtp(existing.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);
                }
                String emailOtpForDisplay = (emailOtpRecord == null || sendEmailOtp) ? null : emailOtpRecord.getOtp();
                String mobileOtpForDisplay = mobileOtpRecord != null ? mobileOtpRecord.getOtp() : null;
                // 11. Send the result back to the screen
                return new RegistrationResult(existing, emailOtpForDisplay, mobileOtpForDisplay);
            }
            // 12. Important: stop here and report an error
            throw new IllegalArgumentException("Email or mobile number already registered");
        }
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        // 13. Security: hide the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.getRoles().add(STUDENT_ROLE);
        // 14. Get or save data in the database
        user = userRepository.save(user);
        // 15. Ask the service to do the main work
        OtpVerification emailOtpRecord = otpService.createAndSendEmailOtp(user.getEmail(), OtpVerification.OtpType.EMAIL_VERIFICATION);
        // 16. Ask the service to do the main work
        OtpVerification mobileOtpRecord = otpService.createAndSendMobileOtp(user.getMobileNumber(), OtpVerification.OtpType.MOBILE_VERIFICATION);
        String emailOtpForDisplay = sendEmailOtp ? null : emailOtpRecord.getOtp();
        String mobileOtpForDisplay = mobileOtpRecord.getOtp();
        // 17. Send the result back to the screen
        return new RegistrationResult(user, emailOtpForDisplay, mobileOtpForDisplay);
    }
    public Optional<User> findByEmail(String email) {
        // 1. Send the result back to the screen
        return userRepository.findByEmail(email);
    }
    public Optional<User> findByMobileNumber(String mobile) {
        // 1. Send the result back to the screen
        return userRepository.findByMobileNumber(mobile);
    }
    public Optional<User> findByEmailOrMobile(String emailOrMobile) {
        // 1. Send the result back to the screen
        return userRepository.findByEmail(emailOrMobile)
                // 2. Get or save data in the database
                .or(() -> userRepository.findByMobileNumber(emailOrMobile));
    }
    @Transactional
    public void markEmailVerified(String email) {
        // 1. Get or save data in the database
        userRepository.findByEmail(email).ifPresent(u -> {
            u.setEmailVerified(true);
            // 2. Get or save data in the database
            userRepository.save(u);
        });
    }
    @Transactional
    public void markMobileVerified(String mobile) {
        // 1. Get or save data in the database
        userRepository.findByMobileNumber(mobile).ifPresent(u -> {
            u.setMobileVerified(true);
            // 2. Get or save data in the database
            userRepository.save(u);
        });
    }
    @Transactional
    public void updatePassword(Long userId, String newPasswordEncoded) {
        // 1. Get or save data in the database
        userRepository.findById(userId).ifPresent(u -> {
            u.setPassword(newPasswordEncoded);
            // 2. Get or save data in the database
            userRepository.save(u);
        });
    }
}
