package com.project.chat_pdf.api.chat.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat_pdf.api.chat.application.dto.ChatMsgCreateDTO;
import com.project.chat_pdf.api.chat.domain.ChatMsg;
import com.project.chat_pdf.api.chat.domain.ChatRoom;
import com.project.chat_pdf.api.chat.infrastructure.ChatMsgMapper;
import com.project.chat_pdf.api.chat.infrastructure.ChatRoomMapper;
import com.project.chat_pdf.api.chat.value.SendType;
import com.project.chat_pdf.api.chat.value.Sender;
import com.project.chat_pdf.util.ChatPdfUtil;
import com.project.chat_pdf.util.ResponseProcessorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatPdfUtil chatPdfUtil;

    private final ChatRoomMapper chatRoomMapper;

    private final ChatMsgMapper chatMsgMapper;

    private final String MSG = "해당 파일 요약본이랑 질문 예시 좀 줘";

    public JsonNode sendMessage(ChatMsgCreateDTO createDTO) throws Exception {

        String content = "";

        if ( SendType.FIRST == createDTO.getSendType() ) {
            content = MSG;
        } else {
            content = createDTO.getContent();

            ChatMsg chatMsg = createDTO.toEntity(content, createDTO.getSender(), chatMsgMapper.findNextOrder(createDTO.getRoomSeq()));
            chatMsgMapper.create(chatMsg);
        }

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("sourceId", createDTO.getApiId());

        Map<String, String> message = new HashMap<>();
        message.put("role", String.valueOf(Sender.user));
        message.put("content", content);
        jsonMap.put("messages", new Object[]{message});

        // ObjectMapper로 JSON 문자열 생성
        ObjectMapper objectMapper = new ObjectMapper();
        String data = objectMapper.writeValueAsString(jsonMap);

        JsonNode jsonNode = ResponseProcessorUtil.processResponse(chatPdfUtil.sendMessage(data), "[ChatService.sendMessage]");

        ChatMsg chatMsg = createDTO.toEntity(jsonNode.get("content").asText(), Sender.assistant
                                , chatMsgMapper.findNextOrder(createDTO.getRoomSeq()));
        chatMsgMapper.create(chatMsg);

        return jsonNode;
    }

    public Long roomCreate(ChatRoom chatRoom) {
        return chatRoomMapper.create(chatRoom);
    }
}
