package com.springboot.wallet_application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.wallet_application.enums.CurrencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CurrencyConversionServiceTest {

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private CurrencyConversionService currencyConversionService;

    @Mock
    private HttpClient httpClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }


    @Test
    void testExceptionFor500StatusCodeResponse() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(500);
        when(httpResponse.body()).thenReturn("Internal Server Error");

        assertThrows(RuntimeException.class, () ->
                currencyConversionService.convert(CurrencyType.USD, CurrencyType.INR, 100));
    }

    @Test
    void testExceptionConvertInvalidResponseType() throws Exception {
        // Prepare mock response with invalid type
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("converted_amount", "invalid_type");
        String responseBodyJson = objectMapper.writeValueAsString(responseBody);

        // Mock HTTP client behavior
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(responseBodyJson);

        assertThrows(RuntimeException.class, () ->
                currencyConversionService.convert(CurrencyType.USD, CurrencyType.INR, 100));

    }
}