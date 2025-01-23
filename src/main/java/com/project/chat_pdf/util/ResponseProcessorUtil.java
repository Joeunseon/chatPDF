package com.project.chat_pdf.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseProcessorUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 공통 response 처리
     * @param responseEntity
     * @param errorMessage
     * @return
     */
    public static JsonNode processResponse(ResponseEntity<String> responseEntity, String errorMessage) {

        try {
            
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return objectMapper.readTree(responseEntity.getBody());
            } else {
                log.error(errorMessage + " statusCode: " + responseEntity.getStatusCode());
                throw new RuntimeException("Failed to fetch data: " + responseEntity.getStatusCode());
            }

        } catch (Exception e) {
           log.error(errorMessage, e);
           throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
