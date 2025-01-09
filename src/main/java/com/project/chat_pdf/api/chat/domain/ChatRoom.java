package com.project.chat_pdf.api.chat.domain;

import com.project.chat_pdf.api.chat.value.DelYn;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoom {

    // @Id
    // 채팅방SEQ
    private Long roomSeq;

    // @Id
    // 파일SEQ
    private Long fileSeq;

    // API ID
    private String apiId;

    // 채팅방 제목
    private String title;

    // 삭제여부 (Y/N)
    private DelYn delYn;

    // 등록일시
    private String regDt;

    // 수정일시
    private String updDt;

    @Builder
    public ChatRoom(Long roomSeq, Long fileSeq, String apiId, String title, DelYn delYn, String regDt, String updDt) {

        this.roomSeq = roomSeq;
        this.fileSeq = fileSeq;
        this.apiId = apiId;
        this.title = title;
        this.delYn = delYn;
        this.regDt = regDt;
        this.updDt = updDt;
    }
}
