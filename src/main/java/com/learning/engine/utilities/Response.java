package com.learning.engine.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class Response {
    private static final ObjectMapper mapper = new ObjectMapper();

   public Map<String, Object>  createSuccessResponse(Map<String, Object> result) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
       response.put("statusCode", 200);
       response.put("body", mapper.writeValueAsString(result));
       response.put("headers", generateCorsHeaders());
        return response;
    }

    public Map<String, Object>  createErrorResponse() throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 500);
        response.put("body", "{\"error\":\"Error handling request\"}");
        response.put("headers", generateCorsHeaders());
        return response;
    }

    private Map<String, String> generateCorsHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        return headers;
    }
}
