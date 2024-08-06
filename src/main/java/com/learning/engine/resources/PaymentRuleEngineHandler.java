package com.learning.engine.resources;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.engine.services.PaymentRuleEngineService;
import com.learning.engine.utilities.Response;
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
    Response customResponse;

    @Inject
    PaymentRuleEngineService paymentRuleEngineService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LOG.info("input is : " + input);
        Map<String, Object> bodyMap = new HashMap<>();
        Map<String, Object> response = new HashMap<>();
        try {
            // Log the entire input
            LOG.info("input is : " + input);

            // Extract the body as a JSON string
            String bodyJson = (String) input.get("body");
            LOG.info("Extracted body JSON:" + bodyJson);

            // Parse the JSON string to a map
            bodyMap = mapper.readValue(bodyJson, Map.class);
            LOG.info("Converted body JSON to Map: " + bodyMap);

            Map<String, Object> result  = new HashMap<>(bodyMap);
            LOG.info("BodyMap is : " + bodyMap);

            response = customResponse.createSuccessResponse(paymentRuleEngineService.processPayment(bodyMap,result));

        } catch (Exception e) {
            LOG.error("Error handling request", e);
            try {
               response =  customResponse.createErrorResponse();
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
        }

        return response;
    }




}
