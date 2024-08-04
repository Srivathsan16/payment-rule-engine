package com.learning.engine;


import com.amazonaws.services.lambda.runtime.Context;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.mvel2.MVEL;
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
public class PaymentRuleEngineHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

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

    private boolean evaluateRule(Object criteria, Map<String, Object> input) {
        System.out.println("Criteria: " + criteria);
        System.out.println("Input: " + input);
        String expression = (String) criteria;
        return MVEL.evalToBoolean(expression, input);
    }

    private void executeAction(Object action, Map<String, Object> result) {
        System.out.println("Action: " + action);
        String expression = (String) action;
        MVEL.eval(expression, result);
    }


}
