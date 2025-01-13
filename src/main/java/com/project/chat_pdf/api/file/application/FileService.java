package com.project.chat_pdf.api.file.application;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.chat_pdf.api.chat.application.dto.ChatRoomCreateDTO;
import com.project.chat_pdf.api.chat.domain.ChatRoom;
import com.project.chat_pdf.api.chat.infrastructure.ChatRoomMapper;
import com.project.chat_pdf.api.chat.value.DelYn;
import com.project.chat_pdf.api.file.application.dto.FileDTO;
import com.project.chat_pdf.api.file.domain.FileInfo;
import com.project.chat_pdf.api.file.infrastructure.FileInfoMapper;
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

    private final FileInfoMapper fileMapper;

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
            FileInfo create = fileUtil.uploadFile(file);
            
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
                                                .title(create.getOriNm())
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

    public ResponseEntity<Resource> findById(Long fileSeq) {

        try {
            FileDTO dto = fileMapper.findById(fileSeq);

            if ( StringUtils.isNotBlank(dto.getPath()) && StringUtils.isNotBlank(dto.getStreNm()) ) {
                Resource resource = fileUtil.downloadFile(dto.getPath(), dto.getStreNm());

                if ( resource != null ) {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" 
                        + URLEncoder.encode((StringUtils.isNotBlank(dto.getOriNm()) ? dto.getOriNm() : dto.getStreNm()), "UTF-8"));

                    return ResponseEntity.ok().headers(headers).body(resource);
                }
            } 
            
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            log.error("[fileService.findById]: ");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
