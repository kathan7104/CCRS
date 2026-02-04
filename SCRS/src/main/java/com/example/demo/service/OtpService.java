// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import com.example.demo.repository.OtpVerificationRepository;
// Import statement: brings a class into scope by name.
import org.slf4j.Logger;
// Import statement: brings a class into scope by name.
import org.slf4j.LoggerFactory;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.mail.SimpleMailMessage;
// Import statement: brings a class into scope by name.
import org.springframework.mail.javamail.JavaMailSender;
// Import statement: brings a class into scope by name.
import org.springframework.scheduling.annotation.Async;

// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Service;

// Import statement: brings a class into scope by name.
import java.security.SecureRandom;
// Import statement: brings a class into scope by name.
import java.time.LocalDateTime;
// Import statement: brings a class into scope by name.
import java.util.Optional;

// Comment: explains code for readers.
/**
 // Comment: explains code for readers.
 * Service that creates, sends and verifies OTPs for email, mobile and forgot-password flows.
 // Comment: explains code for readers.
 * Email sending is done asynchronously; SMS sending is currently a placeholder (logs).
 // Comment: explains code for readers.
 */
// Annotation: adds metadata used by frameworks/tools.
@Service
// Class declaration: defines a new type.
public class OtpService {

    // Method or constructor declaration.
    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    // Field declaration: defines a member variable.
    private static final int OTP_LENGTH = 6;
    // Field declaration: defines a member variable.
    private static final int VALID_MINUTES = 10;

    // Field declaration: defines a member variable.
    private final OtpVerificationRepository otpRepository;
    // Field declaration: defines a member variable.
    private final JavaMailSender mailSender;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${spring.mail.username: noreply@ccrs.edu}")
    // Field declaration: defines a member variable.
    private String fromEmail;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.otp.send-email: true}")
    // Field declaration: defines a member variable.
    private boolean sendEmailOtp;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${ccrs.otp.send-sms:false}")
    // Field declaration: defines a member variable.
    private boolean sendSmsOtp;

    // Field declaration: defines a member variable.
    private final SmsSender smsSender;

    // Opens a method/constructor/block.
    public OtpService(OtpVerificationRepository otpRepository, JavaMailSender mailSender, SmsSender smsSender) {
        // Uses current object (this) to access a field or method.
        this.otpRepository = otpRepository;
        // Uses current object (this) to access a field or method.
        this.mailSender = mailSender;
        // Uses current object (this) to access a field or method.
        this.smsSender = smsSender;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Generate a numeric OTP of fixed length.
    // Opens a method/constructor/block.
    public String generateOtp() {
        // Statement: SecureRandom random = new SecureRandom();
        SecureRandom random = new SecureRandom();
        // Statement: StringBuilder sb = new StringBuilder(OTP_LENGTH);
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        // Opens a method/constructor/block.
        for (int i = 0; i < OTP_LENGTH; i++) {
            // Statement: sb.append(random.nextInt(10));
            sb.append(random.nextInt(10));
        // Closes the current code block.
        }
        // Return: sends a value back to the caller.
        return sb.toString();
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Create an OTP record for the given email and send it by email.
    // Opens a method/constructor/block.
    public OtpVerification createAndSendEmailOtp(String email, OtpVerification.OtpType type) {
        // Statement: String otp = generateOtp();
        String otp = generateOtp();
        // Statement: OtpVerification record = new OtpVerification(email, otp, type, VALID_MINUTES);
        OtpVerification record = new OtpVerification(email, otp, type, VALID_MINUTES);
        // Statement: otpRepository.save(record);
        otpRepository.save(record);
        // Statement: sendEmailOtp(email, otp, type);
        sendEmailOtp(email, otp, type);
        // Return: sends a value back to the caller.
        return record;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Create an OTP record for the given mobile number and send it by SMS (placeholder).
    // Opens a method/constructor/block.
    public OtpVerification createAndSendMobileOtp(String mobileNumber, OtpVerification.OtpType type) {
        // Statement: String otp = generateOtp();
        String otp = generateOtp();
        // Statement: OtpVerification record = new OtpVerification(mobileNumber, otp, type, VALID_M...
        OtpVerification record = new OtpVerification(mobileNumber, otp, type, VALID_MINUTES);
        // Statement: otpRepository.save(record);
        otpRepository.save(record);
        // Statement: sendSmsOtp(mobileNumber, otp, type);
        sendSmsOtp(mobileNumber, otp, type);
        // Return: sends a value back to the caller.
        return record;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Create an OTP linked to a user id for forgot-password flow and send by email.
    // Opens a method/constructor/block.
    public OtpVerification createForgotPasswordOtp(String email, Long userId) {
        // Statement: String otp = generateOtp();
        String otp = generateOtp();
        // Statement: OtpVerification record = new OtpVerification(email, otp, OtpVerification.OtpT...
        OtpVerification record = new OtpVerification(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD, VALID_MINUTES);
        // Statement: record.setUserId(userId);
        record.setUserId(userId);
        // Statement: otpRepository.save(record);
        otpRepository.save(record);
        // Statement: sendEmailOtp(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD);
        sendEmailOtp(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD);
        // Return: sends a value back to the caller.
        return record;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Send OTP by email asynchronously. If `sendEmailOtp` is false, just log for development.
    // Annotation: adds metadata used by frameworks/tools.
    @Async
    // Opens a method/constructor/block.
    public void sendEmailOtp(String to, String otp, OtpVerification.OtpType type) {
        // Statement: String subject = type == OtpVerification.OtpType.FORGOT_PASSWORD
        String subject = type == OtpVerification.OtpType.FORGOT_PASSWORD
                // Statement: ? "CCRS - Reset Password OTP"
                ? "CCRS - Reset Password OTP"
                // Statement: : "CCRS - Verification OTP";
                : "CCRS - Verification OTP";
        // Statement: String body = "Your OTP is: " + otp + ". Valid for " + VALID_MINUTES + " minu...
        String body = "Your OTP is: " + otp + ". Valid for " + VALID_MINUTES + " minutes. Do not share.";
        // Opens a method/constructor/block.
        if (sendEmailOtp) {
            // Opens a new code block.
            try {
                // Statement: SimpleMailMessage msg = new SimpleMailMessage();
                SimpleMailMessage msg = new SimpleMailMessage();
                // Statement: msg.setFrom(fromEmail);
                msg.setFrom(fromEmail);
                // Statement: msg.setTo(to);
                msg.setTo(to);
                // Statement: msg.setSubject(subject);
                msg.setSubject(subject);
                // Statement: msg.setText(body);
                msg.setText(body);
                // Statement: mailSender.send(msg);
                mailSender.send(msg);
                // Statement: log.info("OTP email sent to {}", to);
                log.info("OTP email sent to {}", to);
            // Opens a method/constructor/block.
            } catch (Exception e) {
                // Statement: log.warn("Could not send OTP email to {}: {}", to, e.getMessage());
                log.warn("Could not send OTP email to {}: {}", to, e.getMessage());
            // Closes the current code block.
            }
        // Opens a new code block.
        } else {
            // Statement: log.info("OTP for {} ({}): {} (email sending disabled)", to, type, otp);
            log.info("OTP for {} ({}): {} (email sending disabled)", to, type, otp);
        // Closes the current code block.
        }
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Send OTP by SMS using Twilio REST API if configured. Falls back to logging.
    // Annotation: adds metadata used by frameworks/tools.
    @Async
    // Opens a method/constructor/block.
    private void sendSmsOtp(String mobile, String otp, OtpVerification.OtpType type) {
        // Opens a method/constructor/block.
        if (!sendSmsOtp) {
            // Statement: log.info("OTP for mobile {} ({}): {} (SMS sending disabled - dummy mode)", mo...
            log.info("OTP for mobile {} ({}): {} (SMS sending disabled - dummy mode)", mobile, type, otp);
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }
        // Statement: smsSender.send(mobile, otp, type, VALID_MINUTES);
        smsSender.send(mobile, otp, type, VALID_MINUTES);
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Verify an OTP: check validity, mark used and return the record when valid.
    // Opens a method/constructor/block.
    public Optional<OtpVerification> verifyOtp(String identifier, String otp, OtpVerification.OtpType type) {
        // Statement: Optional<OtpVerification> valid = otpRepository.findValidOtp(
        Optional<OtpVerification> valid = otpRepository.findValidOtp(
                // Statement: identifier, otp, type, LocalDateTime.now());
                identifier, otp, type, LocalDateTime.now());
        // Opens a new code block.
        valid.ifPresent(v -> {
            // Statement: v.setUsed(true);
            v.setUsed(true);
            // Statement: otpRepository.save(v);
            otpRepository.save(v);
        // Statement: });
        });
        // Return: sends a value back to the caller.
        return valid;
    // Closes the current code block.
    }

    // Comment: explains code for readers.
    // Mark all OTPs for an identifier and type as used.
    // Opens a method/constructor/block.
    public void invalidateOtpsForIdentifier(String identifier, OtpVerification.OtpType type) {
        // Statement: otpRepository.markUsedByIdentifierAndType(identifier, type);
        otpRepository.markUsedByIdentifierAndType(identifier, type);
    // Closes the current code block.
    }
// Closes the current code block.
}
