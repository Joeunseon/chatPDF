package com.project.chat_pdf.api.file.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
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
     * Get multipart configuration
     * @return
     */
    public Map<String, Object> getMultipartConfig() {
        return Map.of(
            "maxFileSize", fileUtil.getMaxFileSize(),
            "maxRequestSize", fileUtil.getMaxRequestSize()
        );
    }

    /**
     * 파일 업로드 및 데이터베이스 반영
     * @param file
     * @return
     */
    public JsonNode create(MultipartFile file) {
        
        try {
            // 1. 파일 저장 및 데이터베이스 반영
            FileInfo fileInfo = saveFileToDatabase(file);
            
            // 2. 임시 파일 생성
            File tempFile = createTempFile(file);
            
            // 3. 외부 API 호출 및 응답 처리
            JsonNode jsonNode =processFileWithExternalService(tempFile);

            // 4. 채팅방 데이터베이스 반영
            String apiId = jsonNode.get("sourceId").asText();
            ChatRoomCreateDTO roomCreateDTO = ChatRoomCreateDTO.builder()
                                                .fileSeq(fileInfo.getFileSeq())
                                                .apiId(apiId)
                                                .title(fileInfo.getOriNm())
                                                .delYn(DelYn.N)
                                                .build();
            
            ChatRoom chatRoom = roomCreateDTO.toEntity();
            chatRoomMapper.create(chatRoom);

            // 5. JSON 응답에 추가 데이터 포함
            ObjectNode objectNode = (ObjectNode) jsonNode;
            objectNode.put("roomSeq", chatRoom.getRoomSeq());
            return objectNode;

        } catch (IOException e) {
            log.error("[FileService.create]: File IO error", e);
            throw new RuntimeException("File processing error", e);
        } catch (Exception e) {
            log.error("[FileService.create]: Unexpected error", e);
            throw new RuntimeException("Unexpected error occurred", e);
        }

    }

    /**
     * 파일 취득
     * @param fileSeq
     * @return
     * @throws FileNotFoundException
     * @throws Exception
     */
    public Resource findById(Long fileSeq) throws FileNotFoundException, Exception {
        FileDTO fileDTO = fileMapper.findById(fileSeq);

        if ( fileDTO == null || StringUtils.isBlank(fileDTO.getPath()) || StringUtils.isBlank(fileDTO.getStreNm()) ) {
            throw new FileNotFoundException("File not found with ID: " + fileSeq);
        }

        Resource resource = fileUtil.downloadFile(fileDTO.getPath(), fileDTO.getStreNm());
        if ( resource == null ) {
            throw new FileNotFoundException("Resource not found for file ID: " + fileSeq);
        }

        return resource;
    }

    private FileInfo saveFileToDatabase(MultipartFile file) throws Exception {
        FileInfo fileInfo = fileUtil.uploadFile(file);
        fileMapper.create(fileInfo);
        return fileInfo;
    }

    private File createTempFile(MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);
        tempFile.deleteOnExit();
        return tempFile;
    }

    private JsonNode processFileWithExternalService(File tempFile) throws IOException {
        return ResponseProcessorUtil.processResponse(
            chatPdfUtil.addPDFFile(tempFile), "[FileService.addPDFFile]"
        );
    }
}
