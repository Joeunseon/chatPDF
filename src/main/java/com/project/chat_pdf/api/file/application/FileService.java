package com.project.chat_pdf.api.file.application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.chat_pdf.api.chat.application.dto.ChatRoomCreateDTO;
import com.project.chat_pdf.api.chat.domain.ChatRoom;
import com.project.chat_pdf.api.chat.infrastructure.ChatRoomMapper;
import com.project.chat_pdf.api.chat.value.DelYn;
import com.project.chat_pdf.api.file.infrastructure.FileMapper;
import com.project.chat_pdf.util.ChatPdfUtil;
import com.project.chat_pdf.util.FileUtil;
import com.project.chat_pdf.util.ResponseProcessorUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileUtil fileUtil;

    private final ChatPdfUtil chatPdfUtil;

    private final FileMapper fileMapper;

    private final ChatRoomMapper chatRoomMapper;

    /**
     * property file size 취득
     * @return
     */
    public ResponseEntity<Map<String, Object>> getMultipartConfig() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("maxFileSize", fileUtil.getMaxFileSize());
        map.put("maxRequestSize", fileUtil.getMaxRequestSize());

        log.info("file property: " + map.toString());

        return ResponseEntity.ok().body(map);
    }

    public JsonNode create(MultipartFile file) {
        
        try {
            // file upload
            com.project.chat_pdf.api.file.domain.File create = fileUtil.uploadFile(file);
            
            // file create
            fileMapper.create(create);
            
            File tempFile = File.createTempFile("upload-", file.getOriginalFilename());

            // MultipartFile의 내용을 File로 전송
            file.transferTo(tempFile);

            // JVM 종료 시 임시 파일 삭제
            tempFile.deleteOnExit();
            
            JsonNode jsonNode = ResponseProcessorUtil.processResponse(chatPdfUtil.addPDFFile(tempFile), "[FileService.addPDFFile]");
            String apiId = jsonNode.get("sourceId").asText();

            ChatRoomCreateDTO roomCreateDTO = ChatRoomCreateDTO.builder()
                                                .fileSeq(create.getFileSeq())
                                                .apiId(apiId)
                                                .delYn(DelYn.N)
                                                .build();
            
            ChatRoom chatRoom = roomCreateDTO.toEntity();
            chatRoomMapper.create(chatRoom);

            ObjectNode objectNode = (ObjectNode) jsonNode;

            objectNode.put("roomSeq", chatRoom.getRoomSeq());

            return objectNode;

        } catch (Exception e) {
            log.error("[FileService.create]: ");
            e.printStackTrace();
            return null;
        }

    }
}
