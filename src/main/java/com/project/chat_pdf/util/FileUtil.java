package com.project.chat_pdf.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.project.chat_pdf.api.file.application.dto.FileCreateDTO;
import com.project.chat_pdf.api.file.domain.FileInfo;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FileUtil {

    @Value("${spring.servlet.multipart.maxFileSize}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.maxRequestSize}")
    private String maxRequestSize;

    @Value("${spring.servlet.multipart.location}")
    private String uploadPath;

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }

    /**
     * 파일 업로드
     * @param file
     * @return
     * @throws Exception
     */
    public FileInfo uploadFile(MultipartFile file) throws Exception {

        validateFile(file);

        String basePath = getBasePath();
        createDirectoryIfNotExists(basePath);

        String oriNm = file.getOriginalFilename();
        validateFilename(oriNm);

        String extsn = getFileExtension(oriNm);
        String streNm = getUniqueFileNm(generateTimestampedFileName(extsn), basePath);
        
        Path filePath = Paths.get(basePath, streNm);
        
        // 파일 업로드
        saveFile(file, filePath);

        FileCreateDTO createDTO = FileCreateDTO.builder()
                                    .streNm(streNm)
                                    .oriNm(oriNm)
                                    .path(basePath)
                                    .extsn(extsn)
                                    .size(file.getSize())
                                    .build();

        return createDTO.toEntity();
    }

    /**
     * 파일 다운로드
     * @param strePath
     * @param strNm
     * @return
     * @throws Exception
     */
    public Resource downloadFile(String strePath, String strNm) throws Exception {

        // 파일 경로 설정
        String filePath = strePath + File.separator + strNm;

        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            log.error("[FileUtil.downloadFile] File not found or unreadable: {}", filePath);
            throw new FileNotFoundException("[FileUtil.downloadFile] File not found: " + filePath);
        }

        return new InputStreamResource(Files.newInputStream(path));
    }

    /**
     * 파일 유효성확인
     * @param file
     * @throws Exception
     */
    private void validateFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) 
            throw new IllegalArgumentException("[FileUtil] File is blank");
    }

    /**
     * 기존 저장경로 취득 (년월)
     * @return
     */
    private String getBasePath() {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return uploadPath + File.separator + datePath;
    }

    /**
     * 디렉토리 생성 및 확인
     * @param basePath
     * @throws Exception
     */
    private void createDirectoryIfNotExists(String basePath) throws Exception {
        File directory = new File(basePath);
        if ( !directory.exists() && !directory.mkdirs() )
            throw new IOException("[FileUtil] Failed to create directory: " + basePath);
        
    }

    /**
     * 파일명 유효성검사
     * @param filename
     * @throws Exception
     */
    private void validateFilename(String filename) throws Exception {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("[FileUtil] File name is blank");
        }
    }

    /**
     * 확장자 취득
     * @param filename
     * @return
     */
    private String getFileExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 저장 파일명 취득(날짜_시간)
     * @param extension
     * @return
     */
    private String generateTimestampedFileName(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return timestamp + "." + extension;
    }

    /**
     * 유니크 파일명 취득
     * @param fileNm
     * @param basePath
     * @return
     */
    private String getUniqueFileNm(String fileNm, String basePath) {
        File file = new File(basePath, fileNm);

        if ( !file.exists() )
            return fileNm;

        int cnt = 0;
        String baseNm = fileNm.substring(0, fileNm.lastIndexOf("."));
        String extsn = fileNm.substring(fileNm.lastIndexOf("."));
        while (file.exists()) {
            cnt++;
            file = new File(basePath, baseNm + "_" + cnt + extsn);
        }

        return file.getName();
    }

    /**
     * 파일 저장
     * @param file
     * @param filePath
     * @throws Exception
     */
    private void saveFile(MultipartFile file, Path filePath) throws Exception {
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("[FileUtil.saveFile] Failed to save file: {}", filePath, e);
            throw new IOException("[FileUtil.saveFile] File save failed: " + filePath, e);
        }
    }
}
