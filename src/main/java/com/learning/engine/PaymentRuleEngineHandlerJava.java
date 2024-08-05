/*
package com.learning.engine;

import com.amazonaws.services.lambda.runtime.Context;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Named("paymentRuleEngineHandler")
@RegisterForReflection
public class PaymentRuleEngineHandlerJava implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Inject
    DynamoDbClient dynamoDbClient;

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        List<Map<String, String>> rules = getRulesFromDynamoDB();
        Map<String, Object> result = new HashMap<>(input);
        System.out.println("Input: " + input);

        for (Map<String, String> rule : rules) {
            if (evaluateRule(rule.get("Criteria"), input)) {
                executeAction(rule.get("Action"), result);
            }
        }

        System.out.println("Result: " + result);
        return result;
    }

    private List<Map<String, String>> getRulesFromDynamoDB() {
        ScanRequest scanRequest = ScanRequest.builder().tableName("PaymentRules").build();
        ScanResponse result = dynamoDbClient.scan(scanRequest);
        return result.items().stream()
                .map(item -> item.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().s())))
                .collect(Collectors.toList());
    }

    private boolean evaluateRule(String criteria, Map<String, Object> input) {
        try {
            String[] conditions = criteria.split("&&");
            for (String condition : conditions) {
                String[] parts = condition.trim().split("==|!=");
                if (parts.length != 2) {
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].replace("\"", "").trim();
                boolean isEquals = condition.contains("==");

                if (!input.containsKey(key)) {
                    return false;
                }

                String inputValue = String.valueOf(input.get(key));

                if (isEquals && !inputValue.equals(value)) {
                    return false;
                }

                if (!isEquals && inputValue.equals(value)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error evaluating rule: " + e.getMessage());
            return false;
        }
    }

    private void executeAction(String action, Map<String, Object> result) {
        try {
            String[] actions = action.split(";");
            for (String act : actions) {
                String[] parts = act.trim().split("=");
                if (parts.length != 2) {
                    continue;
                }
                String key = parts[0].trim();
                String value = parts[1].replace(";", "").trim();

                if ("true".equals(value)) {
                    result.put(key, true);
                } else if ("false".equals(value)) {
                    result.put(key, false);
                } else {
                    result.put(key, value);
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing action: " + e.getMessage());
        }
    }
}
*/
