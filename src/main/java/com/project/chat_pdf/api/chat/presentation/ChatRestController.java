package com.project.chat_pdf.api.chat.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.chat_pdf.api.chat.application.ChatService;
import com.project.chat_pdf.api.chat.application.dto.ChatMsgCreateDTO;
import com.project.chat_pdf.util.ControllerUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    public ResponseEntity<Map<String, Object>> rooms() {
        
        return chatService.rooms();
    }

    @PostMapping("/chat")
    public ResponseEntity<JsonNode> create(@RequestBody ChatMsgCreateDTO createDTO) {

        return ControllerUtil.handleRequest(() -> {
            
            return chatService.sendMessage(createDTO);
        });
    }
    
}
