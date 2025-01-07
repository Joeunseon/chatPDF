package com.project.chat_pdf.api.chat.domain;

import com.project.chat_pdf.api.chat.value.Sender;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMsg {

    // @Id
    // 채팅SEQ
    private Long chatSeq;

    // @Id
    // 채팅방SEQ
    private Long roomSeq;

    // 보낸사람 (user/assistant)
    private Sender sender;

    // 채팅내용
    private String content;

    // 채팅순서
    private int order;

    // 등록일시
    private String regDt;

    @Builder
    public ChatMsg(Long chatSeq, Long roomSeq, Sender sender, String content, int order, String regDt) {

        this.chatSeq = chatSeq;
        this.roomSeq = roomSeq;
        this.sender = sender;
        this.content = content;
        this.order = order;
        this.regDt = regDt;
    }
}
