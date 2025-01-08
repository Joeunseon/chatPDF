package com.project.chat_pdf.api.chat.application;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.chat_pdf.api.chat.domain.ChatRoom;
import com.project.chat_pdf.api.chat.infrastructure.ChatRoomMapper;
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

    public JsonNode sendMessage(String data) {

        return ResponseProcessorUtil.processResponse(chatPdfUtil.sendMessage(data), "[ChatService.sendMessage]");
    }

    public Long roomCreate(ChatRoom chatRoom) {
        return chatRoomMapper.create(chatRoom);
    }
}
