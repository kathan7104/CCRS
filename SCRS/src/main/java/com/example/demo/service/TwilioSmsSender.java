// Package declaration: groups related classes in a namespace.
package com.example.demo.service;

// Import statement: brings a class into scope by name.
import com.example.demo.entity.OtpVerification;
// Import statement: brings a class into scope by name.
import org.slf4j.Logger;
// Import statement: brings a class into scope by name.
import org.slf4j.LoggerFactory;
// Import statement: brings a class into scope by name.
import org.springframework.beans.factory.annotation.Value;
// Import statement: brings a class into scope by name.
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
// Import statement: brings a class into scope by name.
import org.springframework.stereotype.Component;

// Import statement: brings a class into scope by name.
import java.net.URI;
// Import statement: brings a class into scope by name.
import java.net.URLEncoder;
// Import statement: brings a class into scope by name.
import java.net.http.HttpClient;
// Import statement: brings a class into scope by name.
import java.net.http.HttpRequest;
// Import statement: brings a class into scope by name.
import java.net.http.HttpResponse;
// Import statement: brings a class into scope by name.
import java.nio.charset.StandardCharsets;
// Import statement: brings a class into scope by name.
import java.util.Base64;

// Annotation: adds metadata used by frameworks/tools.
@Component
// Annotation: adds metadata used by frameworks/tools.
@ConditionalOnProperty(name = "ccrs.sms.provider", havingValue = "twilio")
// Class declaration: defines a new type.
public class TwilioSmsSender implements SmsSender {

    // Method or constructor declaration.
    private static final Logger log = LoggerFactory.getLogger(TwilioSmsSender.class);

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${twilio.account-sid:}")
    // Field declaration: defines a member variable.
    private String twilioAccountSid;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${twilio.auth-token:}")
    // Field declaration: defines a member variable.
    private String twilioAuthToken;

    // Annotation: adds metadata used by frameworks/tools.
    @Value("${twilio.from-number:}")
    // Field declaration: defines a member variable.
    private String twilioFromNumber;

    // Annotation: adds metadata used by frameworks/tools.
    @Override
    // Opens a method/constructor/block.
    public void send(String mobile, String otp, OtpVerification.OtpType type, int validMinutes) {
        // Conditional: runs this block only if the condition is true.
        if (twilioAccountSid == null || twilioAccountSid.isBlank()
                // Statement: || twilioAuthToken == null || twilioAuthToken.isBlank()
                || twilioAuthToken == null || twilioAuthToken.isBlank()
                // Opens a method/constructor/block.
                || twilioFromNumber == null || twilioFromNumber.isBlank()) {
            // Statement: log.info("OTP for mobile {} ({}): {} (SMS gateway not configured)", mobile, t...
            log.info("OTP for mobile {} ({}): {} (SMS gateway not configured)", mobile, type, otp);
            // Return: exits the method without a value.
            return;
        // Closes the current code block.
        }
        // Opens a new code block.
        try {
            // Statement: String body = "Your OTP is: " + otp + ". Valid for " + validMinutes + " minut...
            String body = "Your OTP is: " + otp + ". Valid for " + validMinutes + " minutes.";
            // Statement: String payload = "To=" + URLEncoder.encode(mobile, StandardCharsets.UTF_8)
            String payload = "To=" + URLEncoder.encode(mobile, StandardCharsets.UTF_8)
                    // Statement: + "&From=" + URLEncoder.encode(twilioFromNumber, StandardCharsets.UTF_8)
                    + "&From=" + URLEncoder.encode(twilioFromNumber, StandardCharsets.UTF_8)
                    // Statement: + "&Body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);
                    + "&Body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);
            // Statement: String uri = "https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid...
            String uri = "https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json";
            // Statement: String auth = twilioAccountSid + ":" + twilioAuthToken;
            String auth = twilioAccountSid + ":" + twilioAuthToken;
            // Statement: String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(Standar...
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

            // Statement: HttpRequest req = HttpRequest.newBuilder()
            HttpRequest req = HttpRequest.newBuilder()
                    // Statement: .uri(URI.create(uri))
                    .uri(URI.create(uri))
                    // Statement: .header("Authorization", "Basic " + encodedAuth)
                    .header("Authorization", "Basic " + encodedAuth)
                    // Statement: .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    // Statement: .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    // Statement: .build();
                    .build();
            // Statement: HttpClient client = HttpClient.newHttpClient();
            HttpClient client = HttpClient.newHttpClient();
            // Statement: HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofStri...
            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            // Opens a method/constructor/block.
            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                // Statement: log.info("OTP SMS sent to {}", mobile);
                log.info("OTP SMS sent to {}", mobile);
            // Opens a new code block.
            } else {
                // Statement: log.warn("Could not send OTP SMS to {}: status={}, body={}", mobile, resp.sta...
                log.warn("Could not send OTP SMS to {}: status={}, body={}", mobile, resp.statusCode(), resp.body());
            // Closes the current code block.
            }
        // Opens a method/constructor/block.
        } catch (Exception e) {
            // Statement: log.warn("Could not send OTP SMS to {}: {}", mobile, e.getMessage());
            log.warn("Could not send OTP SMS to {}: {}", mobile, e.getMessage());
        // Closes the current code block.
        }
    // Closes the current code block.
    }
// Closes the current code block.
}
