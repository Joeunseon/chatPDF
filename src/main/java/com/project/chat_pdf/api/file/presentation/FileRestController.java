package com.project.chat_pdf.api.file.presentation;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.chat_pdf.api.file.application.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class FileRestController {

    private final FileService fileService;

    @GetMapping("/file/config")
    public ResponseEntity<Map<String, Object>> getMultipartConfig() {
        log.info("Fetching multipart configuration");
        
        Map<String, Object> config = fileService.getMultipartConfig();

        if (config == null || config.isEmpty()) {
            log.warn("Multipart configuration is empty or null");
            return ResponseEntity.noContent().build(); // HTTP 204 No Content
        }

        return ResponseEntity.ok(config); // HTTP 200 OK
    }

    @GetMapping("/file/{fileSeq}")
    public ResponseEntity<Resource> findById(@PathVariable Long fileSeq) throws Exception {
        log.info("Fetching file with ID: {}", fileSeq);
        
        Resource resource = fileService.findById(fileSeq);

        // Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;");

        return ResponseEntity.ok()
            .headers(headers)
            .body(resource);        // HTTP 200 OK
    }

    @PostMapping("/file")
    public ResponseEntity<JsonNode> create(@RequestParam("file") MultipartFile file) {
        log.info("/file/create");
        
        JsonNode response = fileService.create(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);    // HTTP 201 CREATED
    }
    
}
