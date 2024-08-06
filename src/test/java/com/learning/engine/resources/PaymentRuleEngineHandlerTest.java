package com.learning.engine.resources;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.engine.services.PaymentRuleEngineService;
import com.learning.engine.utilities.Response;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@QuarkusTest
@Disabled
public class PaymentRuleEngineHandlerTest {

    private PaymentRuleEngineHandler paymentRuleEngineHandler;

    Response customResponse = new Response();

    @Mock
    DynamoDbClient dynamoDbClient;

    @InjectMocks
    PaymentRuleEngineService paymentRuleEngineService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        when(dynamoDbClient.serviceName()).thenReturn("DynamoDb");
    }

    @Test
    @Disabled
    void testHandleRequestSuccess() throws Exception {
        // Prepare input
        Map<String, Object> input = new HashMap<>();
        input.put("body", "{\"customerType\":\"premium\",\"amount\":8000,\"paymentMethod\":\"CreditCard\",\"country\":\"NORWAY\"}");

        // Mocking the service and response
        Map<String, Object> serviceResult = new HashMap<>();
        serviceResult.put("verificationRequired", true);
        serviceResult.put("approved", false);

        when(paymentRuleEngineService.processPayment(any(Map.class), any(Map.class))).thenReturn(serviceResult);

        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("statusCode", 200);
        successResponse.put("body", mapper.writeValueAsString(serviceResult));
        successResponse.put("headers", generateCorsHeaders());

        when(customResponse.createSuccessResponse(any(Map.class))).thenReturn(successResponse);

        // Mock context
        Context context = org.mockito.Mockito.mock(Context.class);

        // Invoke handler
        Map<String, Object> response = paymentRuleEngineHandler.handleRequest(input, context);

        // Assertions
        assertEquals(200, response.get("statusCode"));
        assertEquals(successResponse.get("body"), response.get("body"));
        assertEquals(successResponse.get("headers"), response.get("headers"));
    }

    @Test
    @Disabled
    void testHandleRequestError() throws Exception {
        // Prepare input
        Map<String, Object> input = new HashMap<>();
        input.put("body", "{\"customerType\":\"premium\",\"amount\":8000,\"paymentMethod\":\"CreditCard\",\"country\":\"NORWAY\"}");

        // Mocking the service and response
        when(paymentRuleEngineService.processPayment(any(Map.class), any(Map.class))).thenThrow(new RuntimeException("Processing error"));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("statusCode", 500);
        errorResponse.put("body", "{\"error\":\"Error handling request\"}");
        errorResponse.put("headers", generateCorsHeaders());

        when(customResponse.createErrorResponse()).thenReturn(errorResponse);

        // Mock context
        Context context = org.mockito.Mockito.mock(Context.class);

        // Invoke handler
        Map<String, Object> response = paymentRuleEngineHandler.handleRequest(input, context);

        // Assertions
        assertEquals(500, response.get("statusCode"));
        assertEquals(errorResponse.get("body"), response.get("body"));
        assertEquals(errorResponse.get("headers"), response.get("headers"));
    }

    private Map<String, String> generateCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return headers;
    }
}
