package com.example.demo.service;
import com.example.demo.entity.OtpVerification;
import com.example.demo.repository.OtpVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
@Service
public class OtpService {
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 6;
    private static final int VALID_MINUTES = 10;
    private final OtpVerificationRepository otpRepository;
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username: noreply@ccrs.edu}")
    private String fromEmail;
    @Value("${ccrs.otp.send-email: true}")
    private boolean sendEmailOtp;
    @Value("${ccrs.otp.send-sms:false}")
    private boolean sendSmsOtp;
    private final SmsSender smsSender;
    public OtpService(OtpVerificationRepository otpRepository, JavaMailSender mailSender, SmsSender smsSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
        this.smsSender = smsSender;
    }
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        // 1. Send the result back to the screen
        return sb.toString();
    }
    public OtpVerification createAndSendEmailOtp(String email, OtpVerification.OtpType type) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(email, otp, type, VALID_MINUTES);
        // 1. Get or save data in the database
        otpRepository.save(record);
        sendEmailOtp(email, otp, type);
        // 2. Send the result back to the screen
        return record;
    }
    public OtpVerification createAndSendMobileOtp(String mobileNumber, OtpVerification.OtpType type) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(mobileNumber, otp, type, VALID_MINUTES);
        // 1. Get or save data in the database
        otpRepository.save(record);
        sendSmsOtp(mobileNumber, otp, type);
        // 2. Send the result back to the screen
        return record;
    }
    public OtpVerification createForgotPasswordOtp(String email, Long userId) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD, VALID_MINUTES);
        record.setUserId(userId);
        // 1. Get or save data in the database
        otpRepository.save(record);
        sendEmailOtp(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD);
        // 2. Send the result back to the screen
        return record;
    }
    @Async
    public void sendEmailOtp(String to, String otp, OtpVerification.OtpType type) {
        String subject = type == OtpVerification.OtpType.FORGOT_PASSWORD
                ? "CCRS - Reset Password OTP"
                : "CCRS - Verification OTP";
        String body = "Your OTP is: " + otp + ". Valid for " + VALID_MINUTES + " minutes. Do not share.";
        // 1. Check a rule -> decide what to do next
        if (sendEmailOtp) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(fromEmail);
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(body);
                mailSender.send(msg);
                // 2. Note: write a log so we can track it
                log.info("OTP email sent to {}", to);
            } catch (Exception e) {
                // 3. Note: write a log so we can track it
                log.warn("Could not send OTP email to {}: {}", to, e.getMessage());
            }
        } else {
            // 4. Note: write a log so we can track it
            log.info("OTP for {} ({}): {} (email sending disabled)", to, type, otp);
        }
    }
    @Async
    private void sendSmsOtp(String mobile, String otp, OtpVerification.OtpType type) {
        // 1. Check a rule -> decide what to do next
        if (!sendSmsOtp) {
            // 2. Note: write a log so we can track it
            log.info("OTP for mobile {} ({}): {} (SMS sending disabled - dummy mode)", mobile, type, otp);
            // 3. Send the result back to the screen
            return;
        }
        smsSender.send(mobile, otp, type, VALID_MINUTES);
    }
    public Optional<OtpVerification> verifyOtp(String identifier, String otp, OtpVerification.OtpType type) {
        // 1. Get or save data in the database
        Optional<OtpVerification> valid = otpRepository.findValidOtp(
                identifier, otp, type, LocalDateTime.now());
        valid.ifPresent(v -> {
            v.setUsed(true);
            // 2. Get or save data in the database
            otpRepository.save(v);
        });
        // 3. Send the result back to the screen
        return valid;
    }
    public void invalidateOtpsForIdentifier(String identifier, OtpVerification.OtpType type) {
        // 1. Get or save data in the database
        otpRepository.markUsedByIdentifierAndType(identifier, type);
    }
}
