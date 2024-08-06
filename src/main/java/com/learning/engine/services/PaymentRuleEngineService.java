package com.learning.engine.services;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.mvel2.MVEL;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@RegisterForReflection
public class PaymentRuleEngineService {
    @Inject
    DynamoDbClient dynamoDbClient;
    private static final Logger LOG = Logger.getLogger(PaymentRuleEngineService.class);

    public Map<String, Object> processPayment(Map<String, Object> bodyMap, Map<String, Object> result) {
        LOG.info("DynamoDB Client Config: " + dynamoDbClient.serviceName());
        List<Map<String, String>> rules = getRulesFromDynamoDB();
        for (Map<String, String> rule : rules) {
            if (evaluateRule(rule.get("Criteria"), bodyMap)) {
                executeAction(rule.get("Action"), result);
            }
        }
        return result;
    }

    private List<Map<String, String>> getRulesFromDynamoDB() {
        LOG.info("Fetching rules from DynamoDB.... Start");
        ScanRequest scanRequest = ScanRequest.builder().tableName("PaymentRules").build();
        LOG.info("Scan Request done...." + scanRequest.toString());

        ScanResponse result = dynamoDbClient.scan(scanRequest);
        LOG.info("Scan Response done...." + result.items());
        return result.items().stream()
                .map(item -> item.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().s())))
                .collect(Collectors.toList());
    }

    private boolean evaluateRule(Object criteria, Map<String, Object> input) {
        LOG.info("Criteria: " + criteria);
        String expression = (String) criteria;
        return MVEL.evalToBoolean(expression, input);
    }

    private void executeAction(Object action, Map<String, Object> result) {
        LOG.info("Action: " + action);
        String expression = (String) action;
        MVEL.eval(expression, result);
    }
}
