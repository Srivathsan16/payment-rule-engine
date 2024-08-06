package com.learning.engine.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@QuarkusTest
public class PaymentRuleEngineServiceTest {

    @Mock
    DynamoDbClient dynamoDbClient;

    @InjectMocks
    PaymentRuleEngineService paymentRuleEngineService;

    private static final Logger LOG = Logger.getLogger(PaymentRuleEngineServiceTest.class);

    @BeforeEach
    void setUp() {
        // Initialize any required setup before each test
    }

    @Test
    void testProcessPayment() {
        // Prepare mock rules from DynamoDB
        Map<String, AttributeValue> rule1 = new HashMap<>();
        rule1.put("Criteria", AttributeValue.builder().s("amount > 5000").build());
        rule1.put("Action", AttributeValue.builder().s("result.put('approved', false)").build());

        Map<String, AttributeValue> rule2 = new HashMap<>();
        rule2.put("Criteria", AttributeValue.builder().s("customerType == 'premium'").build());
        rule2.put("Action", AttributeValue.builder().s("result.put('verificationRequired', true)").build());

        List<Map<String, AttributeValue>> items = Arrays.asList(rule1, rule2);
        ScanResponse scanResponse = ScanResponse.builder().items(items).build();

        when(dynamoDbClient.scan(any(ScanRequest.class))).thenReturn(scanResponse);

        // Prepare input
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("customerType", "premium");
        bodyMap.put("amount", 8000);
        bodyMap.put("paymentMethod", "CreditCard");
        bodyMap.put("country", "NORWAY");

        // Prepare result
        Map<String, Object> result = new HashMap<>();

        // Invoke service method
        Map<String, Object> response = paymentRuleEngineService.processPayment(bodyMap, result);

        // Assertions
        assertTrue((Boolean) response.get("verificationRequired"));
        assertTrue(!(Boolean) response.get("approved"));
    }
}
