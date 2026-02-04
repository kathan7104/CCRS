package com.example.demo.service;

import com.example.demo.entity.OtpVerification;
import com.example.demo.repository.OtpVerificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service that creates, sends and verifies OTPs for email, mobile and forgot-password flows.
 * Email sending is done asynchronously; SMS sending is currently a placeholder (logs).
 */
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

    @Value("${twilio.account-sid:}")
    private String twilioAccountSid;

    @Value("${twilio.auth-token:}")
    private String twilioAuthToken;

    @Value("${twilio.from-number:}")
    private String twilioFromNumber;

    public OtpService(OtpVerificationRepository otpRepository, JavaMailSender mailSender) {
        this.otpRepository = otpRepository;
        this.mailSender = mailSender;
    }

    // Generate a numeric OTP of fixed length.
    public String generateOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    // Create an OTP record for the given email and send it by email.
    public OtpVerification createAndSendEmailOtp(String email, OtpVerification.OtpType type) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(email, otp, type, VALID_MINUTES);
        otpRepository.save(record);
        sendEmailOtp(email, otp, type);
        return record;
    }

    // Create an OTP record for the given mobile number and send it by SMS (placeholder).
    public OtpVerification createAndSendMobileOtp(String mobileNumber, OtpVerification.OtpType type) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(mobileNumber, otp, type, VALID_MINUTES);
        otpRepository.save(record);
        sendSmsOtp(mobileNumber, otp, type);
        return record;
    }

    // Create an OTP linked to a user id for forgot-password flow and send by email.
    public OtpVerification createForgotPasswordOtp(String email, Long userId) {
        String otp = generateOtp();
        OtpVerification record = new OtpVerification(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD, VALID_MINUTES);
        record.setUserId(userId);
        otpRepository.save(record);
        sendEmailOtp(email, otp, OtpVerification.OtpType.FORGOT_PASSWORD);
        return record;
    }

    // Send OTP by email asynchronously. If `sendEmailOtp` is false, just log for development.
    @Async
    public void sendEmailOtp(String to, String otp, OtpVerification.OtpType type) {
        String subject = type == OtpVerification.OtpType.FORGOT_PASSWORD
                ? "CCRS - Reset Password OTP"
                : "CCRS - Verification OTP";
        String body = "Your OTP is: " + otp + ". Valid for " + VALID_MINUTES + " minutes. Do not share.";
        if (sendEmailOtp) {
            try {
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setFrom(fromEmail);
                msg.setTo(to);
                msg.setSubject(subject);
                msg.setText(body);
                mailSender.send(msg);
                log.info("OTP email sent to {}", to);
            } catch (Exception e) {
                log.warn("Could not send OTP email to {}: {}", to, e.getMessage());
            }
        } else {
            log.info("OTP for {} ({}): {} (email sending disabled)", to, type, otp);
        }
    }

    // Send OTP by SMS using Twilio REST API if configured. Falls back to logging.
    @Async
    private void sendSmsOtp(String mobile, String otp, OtpVerification.OtpType type) {
        if (twilioAccountSid == null || twilioAccountSid.isBlank()
                || twilioAuthToken == null || twilioAuthToken.isBlank()
                || twilioFromNumber == null || twilioFromNumber.isBlank()) {
            log.info("OTP for mobile {} ({}): {} (SMS gateway not configured)", mobile, type, otp);
            return;
        }
        try {
            String body = "Your OTP is: " + otp + ". Valid for " + VALID_MINUTES + " minutes.";
            String payload = "To=" + URLEncoder.encode(mobile, StandardCharsets.UTF_8)
                    + "&From=" + URLEncoder.encode(twilioFromNumber, StandardCharsets.UTF_8)
                    + "&Body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);
            String uri = "https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json";
            String auth = twilioAccountSid + ":" + twilioAuthToken;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(uri))
                    .header("Authorization", "Basic " + encodedAuth)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                log.info("OTP SMS sent to {}", mobile);
            } else {
                log.warn("Could not send OTP SMS to {}: status={}, body={}", mobile, resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            log.warn("Could not send OTP SMS to {}: {}", mobile, e.getMessage());
        }
    }

    // Verify an OTP: check validity, mark used and return the record when valid.
    public Optional<OtpVerification> verifyOtp(String identifier, String otp, OtpVerification.OtpType type) {
        Optional<OtpVerification> valid = otpRepository.findValidOtp(
                identifier, otp, type, LocalDateTime.now());
        valid.ifPresent(v -> {
            v.setUsed(true);
            otpRepository.save(v);
        });
        return valid;
    }

    // Mark all OTPs for an identifier and type as used.
    public void invalidateOtpsForIdentifier(String identifier, OtpVerification.OtpType type) {
        otpRepository.markUsedByIdentifierAndType(identifier, type);
    }
}
