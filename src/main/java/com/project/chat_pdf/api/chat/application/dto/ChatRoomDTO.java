package com.project.chat_pdf.api.chat.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomDTO {

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
}
