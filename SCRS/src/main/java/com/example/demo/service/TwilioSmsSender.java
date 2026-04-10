/*
 * File: src/main/java/com/example/demo/service/TwilioSmsSender.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;
import com.example.demo.entity.OtpVerification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
// Class Summary: Service class that contains business logic used by controllers.
// @Component registers a generic Spring-managed bean.
@Component
@ConditionalOnProperty(name = "ccrs.sms.provider", havingValue = "twilio")
public class TwilioSmsSender implements SmsSender {
// Field: stores log for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Logger log = LoggerFactory.getLogger(TwilioSmsSender.class);
// @Value injects a property value from application.properties.
    @Value("${twilio.account-sid:}")
// Field: stores twilioAccountSid for this class.
    private String twilioAccountSid;
// @Value injects a property value from application.properties.
    @Value("${twilio.auth-token:}")
// Field: stores twilioAuthToken for this class.
    private String twilioAuthToken;
// @Value injects a property value from application.properties.
    @Value("${twilio.from-number:}")
// Field: stores twilioFromNumber for this class.
    private String twilioFromNumber;
    @Override
// Service method: contains business logic and coordinates repositories.
    public void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes) {
        // 1. Check a rule -> decide what to do next
        if (twilioAccountSid == null || twilioAccountSid.isBlank()
                || twilioAuthToken == null || twilioAuthToken.isBlank()
                || twilioFromNumber == null || twilioFromNumber.isBlank()) {
            // 2. Note: write a log so we can track it
            log.info("OTP for mobile {} ({}): {} (SMS gateway not configured)", mobile, type, otp);
            // 3. Send the result back to the screen
            return;
        }
        try {
            String body = "Your OTP is: " + otp + ". Valid for " + validMinutes + " minutes.";
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
            // 4. Check a rule -> decide what to do next
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                // 5. Note: write a log so we can track it
                log.info("OTP SMS sent to {}", mobile);
            } else {
                // 6. Note: write a log so we can track it
                log.warn("Could not send OTP SMS to {}: status={}, body={}", mobile, resp.statusCode(), resp.body());
            }
        } catch (Exception e) {
            // 7. Note: write a log so we can track it
            log.warn("Could not send OTP SMS to {}: {}", mobile, e.getMessage());
        }
    }
}
