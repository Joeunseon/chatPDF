package com.project.chat_pdf.api.file.presentation;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.chat_pdf.api.file.application.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

}
