package com.project.chat_pdf.util;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatPdfUtil {

    @Value("${chat.pdf.api.url}")
    private String apiUrl;

    @Value("${chat.pdf.api.key}")
    private String apiKey;

    private final HttpsUtil httpsUtil;

    private String addPDFUrl;

    private String chatUrl;

    @PostConstruct
    public void init() {
        addPDFUrl = apiUrl + "/sources/add-file";
        chatUrl = apiUrl + "/chats/message";
    }

    /**
     * 파일 업로드
     * @param file
     * @return
     */
    public ResponseEntity<String> addPDFFile(File file) {
        HttpHeaders headers = createHeaders();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        return httpsUtil.sendPostRequest(addPDFUrl, body, headers);
    }

    /**
     * 채팅 메시지 전송
     * @param data
     * @return
     */
    public ResponseEntity<String> sendMessage(String data) {
        HttpHeaders headers = createHeaders();
        headers.set("Content-Type", "application/json");

        // UTF-8로 인코딩된 JSON 데이터 생성
        String utf8Data = new String(data.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        return httpsUtil.sendPostRequest(chatUrl, utf8Data, headers);
    }

    /**
     * 공통 헤더 생성
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        return headers;
    }
}
