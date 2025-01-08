package com.project.chat_pdf.api.file.application.dto;

import com.project.chat_pdf.api.file.domain.File;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileCreateDTO {

    // @Id
    // 파일SEQ
    private Long fileSeq;

    // 저장파일명
    private String streNm;

    // 원본파일명
    private String oriNm;

    // 파일저장경로
    private String path;

    // 파일확장자
    private String extsn;

    // 파일크기
    private Long size;

    // 등록일시
    private String regDt;

    public File toEntity() {
        return File.builder()
                .streNm(streNm)
                .oriNm(oriNm)
                .path(path)
                .extsn(extsn)
                .size(size)
                .build();
    }
}
