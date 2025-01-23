package com.project.chat_pdf.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class HttpsUtil {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * GET 요청을 처리하는 메서드
     * @param url
     * @param headers
     * @return
     */
    public ResponseEntity<String> sendGetRequest(String url, HttpHeaders headers) {
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    /**
     * POST 요청을 처리하는 메서드
     * @param url
     * @param requestBody
     * @param headers
     * @return
     */
    public ResponseEntity<String> sendPostRequest(String url, Object requestBody, HttpHeaders headers) {
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
    }
}
