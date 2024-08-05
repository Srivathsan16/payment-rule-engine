package com.learning.engine;


import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.jboss.logging.Logger;
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

    private static final Logger LOG = Logger.getLogger(PaymentRuleEngineHandler.class);
    @Inject
    DynamoDbClient dynamoDbClient;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LOG.info("input is : " + input);
        Map<String, Object> bodyMap = new HashMap<>();
        try {
            // Log the entire input
            LOG.info("input is : " +input);

            // Extract the body as a JSON string
            String bodyJson = (String) input.get("body");
            LOG.info("Extracted body JSON:" + bodyJson);

            // Parse the JSON string to a map
            bodyMap = mapper.readValue(bodyJson, Map.class);
            LOG.info("Converted body JSON to Map: " + bodyMap);

        } catch (Exception e) {
            LOG.error("Error handling request", e);
            throw new RuntimeException("Error handling request", e);
        }


        LOG.info("DynamoDB Client Config: " + dynamoDbClient.serviceName());

        List<Map<String, String>> rules = getRulesFromDynamoDB();
        Map<String, Object> result = new HashMap<>(bodyMap);
        LOG.info("BodyMAp is : " + bodyMap);

        for (Map<String, String> rule : rules) {
            if (evaluateRule(rule.get("Criteria"), bodyMap)) {
                executeAction(rule.get("Action"), result);
            }
        }

        // Add CORS headers
        Map<String, Object> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");

        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 200);
        response.put("headers", headers);
        response.put("body", result);

        System.out.println("Result: " + result);
        return response;
    }

    private List<Map<String, String>> getRulesFromDynamoDB() {
        LOG.info("Fetching rules from DynamoDB.... Start");
        ScanRequest scanRequest = ScanRequest.builder().tableName("PaymentRules").build();
        LOG.info("Scan Request done...." +scanRequest.toString());

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
