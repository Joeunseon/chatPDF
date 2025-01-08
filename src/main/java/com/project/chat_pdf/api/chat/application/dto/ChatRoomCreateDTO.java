package com.project.chat_pdf.api.chat.application.dto;

import com.project.chat_pdf.api.chat.domain.ChatRoom;
import com.project.chat_pdf.api.chat.value.DelYn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateDTO {

    // @Id
    // 채팅방SEQ
    private Long roomSeq;

    // @Id
    // 파일SEQ
    private Long fileSeq;

    // API ID
    private String apiId;

    // 삭제여부 (Y/N)
    private DelYn delYn;

    // 등록일시
    private String regDt;

    // 수정일시
    private String updDt;

    public ChatRoom toEntity() {
        return ChatRoom.builder()
                    .fileSeq(fileSeq)
                    .apiId(apiId)
                    .delYn(delYn)
                    .build();
    }
}
