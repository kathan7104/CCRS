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

@Service
public class RazorpayGatewayService {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Pattern STRING_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]*)\"");
    private static final Pattern NUMBER_FIELD = Pattern.compile("\"([^\"]+)\"\\s*:\\s*(\\d+)");

    @Value("${ccrs.payment.razorpay.base-url:https://api.razorpay.com}")
    private String baseUrl;

    @Value("${ccrs.payment.razorpay.key-id:}")
    private String keyId;

    @Value("${ccrs.payment.razorpay.key-secret:}")
    private String keySecret;

    @Value("${ccrs.payment.razorpay.enabled:false}")
    private boolean enabled;

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

    public String getKeyId() {
        validateConfig();
        return keyId;
    }

    private BigDecimal paiseToRupees(long paise) {
        return BigDecimal.valueOf(paise).movePointLeft(2);
    }

    private String basicAuth() {
        String raw = keyId + ":" + keySecret;
        return "Basic " + Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

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

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String extractString(String json, String field) {
        Matcher matcher = STRING_FIELD.matcher(json);
        while (matcher.find()) {
            if (field.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        return "";
    }

    private long extractLong(String json, String field) {
        Matcher matcher = NUMBER_FIELD.matcher(json);
        while (matcher.find()) {
            if (field.equals(matcher.group(1))) {
                return Long.parseLong(matcher.group(2));
            }
        }
        return 0L;
    }

    private String defaultIfBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }

    private void validateConfig() {
        if (!enabled || keyId == null || keyId.isBlank() || keySecret == null || keySecret.isBlank()) {
            throw new IllegalStateException("Razorpay is not configured. Set ccrs.payment.razorpay.enabled=true, key-id, and key-secret.");
        }
    }

    public record RazorpayOrder(String orderId, String currency, long amountPaise, String status) {
    }

    public record RazorpayPayment(String paymentId, String orderId, String status, BigDecimal amount) {
    }
}
