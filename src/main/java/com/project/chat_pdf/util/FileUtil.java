package com.project.chat_pdf.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.project.chat_pdf.api.file.application.dto.FileCreateDTO;

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

    public com.project.chat_pdf.api.file.domain.File uploadFile(MultipartFile file) throws Exception {

        if ( file == null || file.isEmpty() ) 
            throw new Exception("[FileUtil] File blank");

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyyMM");
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        String basePath = uploadPath + File.separator + LocalDateTime.now().format(dateFormat);
        
        File strePath = new File(basePath);
        if ( !strePath.exists() ) {
            if ( !strePath.mkdirs() ) 
                throw new Exception("[FileUtil.mkdirs] saveFolder : Creation Fail");
        }

        String oriNm = file.getOriginalFilename();

        if ( oriNm.isBlank() )
            throw new Exception("[FileUtil] File blank");

        String extsn = oriNm.substring(oriNm.lastIndexOf(".") + 1);
        String streNm = getUniqueFileNm(LocalDateTime.now().format(dateTimeFormat) + "." + extsn, basePath);
        
        Path filePath = Paths.get(basePath, streNm);
        try {
            Files.copy(file.getInputStream(), filePath);
        } catch (IOException e) {
            log.error("[FileUtil.uploadFile]");
            e.printStackTrace();
            throw new Exception("[FileUtil.uploadFile] saveFile : creation Fail");
        }

        FileCreateDTO createDTO = FileCreateDTO.builder()
                                    .streNm(streNm)
                                    .oriNm(oriNm)
                                    .path(basePath)
                                    .extsn(extsn)
                                    .size(file.getSize())
                                    .build();

        return createDTO.toEntity();
    }

    public String getUniqueFileNm(String fileNm, String basePath) {
        File file = new File(basePath, fileNm);

        if ( !file.exists() )
            return fileNm;

        int cnt = 0;
        String baseNm = fileNm.substring(0, fileNm.lastIndexOf("."));
        String extsn = fileNm.substring(fileNm.lastIndexOf("."));
        do {
            cnt++;
            file = new File(basePath, baseNm + "_" + cnt + extsn);
        } while (file.exists());

        return file.getName();
    }
}
