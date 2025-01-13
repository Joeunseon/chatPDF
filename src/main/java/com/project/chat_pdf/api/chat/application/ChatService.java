package com.project.chat_pdf.api.chat.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.chat_pdf.api.chat.application.dto.ChatMsgCreateDTO;
import com.project.chat_pdf.api.chat.application.dto.ChatRoomUpdateDTO;
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

    private final String DEFAULT_MESSAGE = "해당 파일 요약본이랑 질문 예시 좀 줘";

    /**
     * 채팅방 목록 조회
     * @return
     */
    public Map<String, Object> rooms() {

        Map<String, Object> resultMap = new HashMap<>();

        Integer countAll = chatRoomMapper.countAll();
        resultMap.put("resultCnt", countAll);

        if (countAll > 0) {
            resultMap.put("resultList", chatRoomMapper.findAll());
        }

        return resultMap;
    }

    /**
     * 메시지 목록 조회
     * @param roomSeq
     * @return
     */
    public Map<String, Object> msgs(Long roomSeq) {

        Map<String, Object> resultMap = new HashMap<>();

        Integer countAll = chatMsgMapper.countAll(roomSeq);
        resultMap.put("resultCnt", countAll);

        if (countAll > 0) {
            resultMap.put("resultList", chatMsgMapper.findAll(roomSeq));
        }

        return resultMap;
    }

    /**
     * 메시지 전송
     * @param createDTO
     * @return
     * @throws Exception
     */
    public JsonNode sendMessage(ChatMsgCreateDTO createDTO) throws Exception {

        String content = "";

        // 1. 최초 메시지 확인
        if ( SendType.FIRST == createDTO.getSendType() ) {
            content = DEFAULT_MESSAGE;
        } else {
            content = createDTO.getContent();

            // 1-1. 사용자 메시지 저장
            ChatMsg userMessage = createDTO.toEntity(content, createDTO.getSender(), chatMsgMapper.findNextOrder(createDTO.getRoomSeq()));
            chatMsgMapper.create(userMessage);
        }

        // 2. API 요청 데이터 생성
        Map<String, Object> jsonMap = createApiRequestData(createDTO, content);
        String data = new ObjectMapper().writeValueAsString(jsonMap);

        // 3. API 호출 및 응답 처리
        JsonNode jsonNode = ResponseProcessorUtil.processResponse(chatPdfUtil.sendMessage(data), "[ChatService.sendMessage]");

        // 4. API 응답 메시지 저장
        ChatMsg assistantMessage  = createDTO.toEntity(jsonNode.get("content").asText(), Sender.assistant
                                , chatMsgMapper.findNextOrder(createDTO.getRoomSeq()));
        chatMsgMapper.create(assistantMessage);

        return jsonNode;
    }

    /**
     * 채팅방 정보 업데이트
     * @param updateDTO
     * @return
     */
    public Boolean update(ChatRoomUpdateDTO updateDTO) {

        ChatRoom chatRoom = updateDTO.toEntitiy();

        Long result = chatRoomMapper.update(chatRoom);
        return result > 0;
    }

    /**
     * 채팅방 삭제
     * @param roomSeq
     * @return
     */
    public Boolean delete(Long roomSeq) {

        Long result = chatRoomMapper.delete(roomSeq);
        return result > 0;
    }

    private Map<String, Object> createApiRequestData(ChatMsgCreateDTO createDTO, String content) {
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("sourceId", createDTO.getApiId());

        Map<String, String> message = new HashMap<>();
        message.put("role", String.valueOf(Sender.user));
        message.put("content", content);
        jsonMap.put("messages", new Object[]{message});

        return jsonMap;
    }
}
