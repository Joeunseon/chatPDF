package com.project.chat_pdf.api.file.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class File {

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

    @Builder
    public File(Long fileSeq, String streNm, String oriNm, String path, String extsn, Long size, String regDt) {

        this.fileSeq = fileSeq;
        this.streNm = streNm;
        this.oriNm = oriNm;
        this.path = path;
        this.extsn = extsn;
        this.size = size;
        this.regDt = regDt;
    }
}
