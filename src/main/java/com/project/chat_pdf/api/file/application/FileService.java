package com.project.chat_pdf.api.file.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.chat_pdf.util.FilePropertyUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FilePropertyUtil filePropertyUtil;

    /**
     * property file size 취득
     * @return
     */
    public ResponseEntity<Map<String, Object>> getMultipartConfig() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("maxFileSize", filePropertyUtil.getMaxFileSize());
        map.put("maxRequestSize", filePropertyUtil.getMaxRequestSize());

        log.info("file property: " + map.toString());

        return ResponseEntity.ok().body(map);
    }
}
