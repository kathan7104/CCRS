/*
 * File: src/main/java/com/example/demo/service/RazorpayGatewayService.java
 * Role: Service
 * MVC Fit: Contains business logic used by controllers.
 * Connects To: Controller calls Service, Service calls Repository
 */

package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Class Summary: Service class that contains business logic used by controllers.
// @Service marks the business logic layer for Spring to manage as a bean.
@Service
public class RazorpayGatewayService {
// Field: stores httpClient for this class.
// Service method: contains business logic and coordinates repositories.
    private final HttpClient httpClient = HttpClient.newHttpClient();
// Field: stores STRING_FIELD for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern STRING_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
// Field: stores NUMBER_FIELD for this class.
// Service method: contains business logic and coordinates repositories.
    private static final Pattern NUMBER_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)");

// @Value injects a property value from application.properties.
    @Value("${ccrs.payment.razorpay.base-url:https://api.razorpay.com}")
// Field: stores baseUrl for this class.
    private String baseUrl;

// @Value injects a property value from application.properties.
    @Value("${ccrs.payment.razorpay.key-id:}")
// Field: stores keyId for this class.
    private String keyId;

// @Value injects a property value from application.properties.
    @Value("${ccrs.payment.razorpay.key-secret:}")
// Field: stores keySecret for this class.
    private String keySecret;

// @Value injects a property value from application.properties.
    @Value("${ccrs.payment.razorpay.enabled:false}")
// Field: stores enabled for this class.
    private boolean enabled;

// Service method: contains business logic and coordinates repositories.
    public RazorpayOrder createOrder(long amountPaise, String receipt, Map<String, String> notes)
            throws IOException, InterruptedException {
        validateConfig();
        String payload = buildOrderPayload(amountPaise, receipt, notes);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/orders"))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Razorpay order create failed (" + response.statusCode() + "): " + response.body());
        }

        String responseBody = response.body();
        return new RazorpayOrder(
                extractString(responseBody, "id"),
                defaultIfBlank(extractString(responseBody, "currency"), "INR"),
                extractLong(responseBody, "amount"),
                extractString(responseBody, "status")
        );
    }

// Service method: contains business logic and coordinates repositories.
    public RazorpayPayment fetchPayment(String paymentId) throws IOException, InterruptedException {
        validateConfig();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/payments/" + paymentId))
                .header("Authorization", basicAuth())
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
            return null;
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException("Razorpay payment fetch failed (" + response.statusCode() + "): " + response.body());
        }

        String responseBody = response.body();
        return new RazorpayPayment(
                extractString(responseBody, "id"),
                extractString(responseBody, "order_id"),
                extractString(responseBody, "status"),
                paiseToRupees(extractLong(responseBody, "amount"))
        );
    }

// Service method: contains business logic and coordinates repositories.
    public String getKeyId() {
        validateConfig();
        return keyId;
    }

// Service method: contains business logic and coordinates repositories.
    private BigDecimal paiseToRupees(long paise) {
        return BigDecimal.valueOf(paise).movePointLeft(2);
    }

// Service method: contains business logic and coordinates repositories.
    private String basicAuth() {
        String raw = keyId + ":" + keySecret;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

// Service method: contains business logic and coordinates repositories.
    private String buildOrderPayload(long amountPaise, String receipt, Map<String, String> notes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"amount\":").append(amountPaise).append(",");
        sb.append("\"currency\":\"INR\",");
        sb.append("\"receipt\":\"").append(escapeJson(receipt)).append("\",");
        sb.append("\"payment_capture\":1,");
        sb.append("\"notes\":{");
        boolean first = true;
        for (Map.Entry<String, String> entry : notes.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
        }
        sb.append("}}");
        return sb.toString();
    }

// Service method: contains business logic and coordinates repositories.
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

// Service method: contains business logic and coordinates repositories.
    private String extractString(String json, String field) {
        Matcher matcher = STRING_FIELD.matcher(json);
        while (matcher.find()) {
            if (field.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return "";
    }

// Service method: contains business logic and coordinates repositories.
    private long extractLong(String json, String field) {
        Matcher matcher = NUMBER_FIELD.matcher(json);
        while (matcher.find()) {
            if (field.equals(matcher.group(1))) {
                return Long.parseLong(matcher.group(2));
            }
        }
        return 0L;
    }

// Service method: contains business logic and coordinates repositories.
    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

// Service method: contains business logic and coordinates repositories.
    private void validateConfig() {
        if (!enabled || keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
            throw new IllegalStateException("Razorpay is not configured. Set ccrs.payment.razorpay.enabled=true, key-id, and key-secret.");
        }
    }

// Service method: contains business logic and coordinates repositories.
    public record RazorpayOrder(String orderId, String currency, long amountPaise, String status) {
    }

// Service method: contains business logic and coordinates repositories.
    public record RazorpayPayment(String paymentId, String orderId, String status, BigDecimal amount) {
    }
}
