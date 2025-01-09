package com.project.chat_pdf.api.file.presentation;

import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.chat_pdf.api.file.application.FileService;
import com.project.chat_pdf.util.ControllerUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class FileRestController {

    private final FileService fileService;

    @GetMapping("/file/multipart")
    public ResponseEntity<Map<String, Object>> getMultipartConfig() {

        return fileService.getMultipartConfig();
    }

    @GetMapping("/file/{fileSeq}")
    public ResponseEntity<Resource> findById(@PathVariable Long fileSeq) {

        return fileService.findById(fileSeq);
    }

    @PostMapping("/file")
    public ResponseEntity<JsonNode> create(@RequestParam("file") MultipartFile file) {
        log.info("/file/create");
        
        return ControllerUtil.handleRequest(() -> {

            return fileService.create(file);
        });
    }
    
}
