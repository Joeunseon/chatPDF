package com.project.chat_pdf.api.file.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FileDTO {

    // 저장파일명
    private String streNm;

    // 원본파일명
    private String oriNm;

    // 파일저장경로
    private String path;
}
