package com.project.chat_pdf.api.chat.presentation;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.chat_pdf.api.chat.application.ChatService;
import com.project.chat_pdf.api.chat.application.dto.ChatMsgCreateDTO;
import com.project.chat_pdf.api.chat.application.dto.ChatRoomUpdateDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        try {
            Map<String, Object> rooms = chatService.rooms();
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            log.error("Error fetching rooms: ", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/msgs/{roomSeq}")
    public ResponseEntity<Map<String, Object>> msgs(@PathVariable Long roomSeq) {
        
        try {
            Map<String, Object> messages = chatService.msgs(roomSeq);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error fetching messages: ", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping("/chat")
    public ResponseEntity<JsonNode> create(@RequestBody ChatMsgCreateDTO createDTO) {

        try {
            JsonNode response = chatService.sendMessage(createDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending message: ", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PatchMapping("/room")
    public ResponseEntity<Boolean> update(@RequestBody ChatRoomUpdateDTO updateDTO) {

        try {
            boolean isUpdated = chatService.update(updateDTO);
            if (isUpdated) {
                return ResponseEntity.ok(true); // HTTP 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // HTTP 500 Internal Server Error
            }
        } catch (Exception e) {
            log.error("Error updating chat room: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
    
    @DeleteMapping("/room/{roomSeq}")
    public ResponseEntity<Boolean> delete(@PathVariable Long roomSeq) {

        try {
            boolean isDeleted = chatService.delete(roomSeq);
            if (isDeleted) {
                return ResponseEntity.ok(true); // HTTP 200 OK
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false); // HTTP 500 Internal Server Error
            }
        } catch (Exception e) {
            log.error("Error deleting chat room: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }
}
