package com.springboot.wallet_application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.enums.CurrencyType;
import com.springboot.wallet_application.exception.CurrencyConversionException;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
public class CurrencyConversionService {
    private static final String CONVERSION_SERVICE_URL = "http://localhost:8081/convert";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    public CurrencyConversionService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public double convert(CurrencyType fromCurrency, CurrencyType toCurrency, double amount) {
        try {
            // Prepare the request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from_currency", fromCurrency.name());
            requestBody.put("to_currency", toCurrency.name());
            requestBody.put("amount", amount);

            // Convert the request body to JSON
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);

            // Create the HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CONVERSION_SERVICE_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Check if the response status is OK (200)
            if (response.statusCode() != 200) {
                throw new CurrencyConversionException("Failed to convert currency: " + response);
            }

            // Parse the response body
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            Object convertedAmountObj = responseBody.get("converted_amount");

            // Convert the amount to double
            if (convertedAmountObj instanceof Integer) {
                return ((Integer) convertedAmountObj).doubleValue();
            } else if (convertedAmountObj instanceof Double) {
                return (Double) convertedAmountObj;
            } else {
                throw new CurrencyConversionException("Unexpected type for converted_amount: " + convertedAmountObj.getClass().getName());
            }
        } catch (CurrencyConversionException e) {
            throw new CurrencyConversionException("Failed to convert currency: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected Error during currency conversion: " + e.getMessage());
        }
    }
}
