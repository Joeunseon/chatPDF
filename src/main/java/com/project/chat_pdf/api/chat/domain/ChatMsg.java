package com.project.chat_pdf.api.chat.domain;

import com.project.chat_pdf.api.chat.value.SendType;
import com.project.chat_pdf.api.chat.value.Sender;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMsg {

    // @Id
    // 메시지SEQ
    private Long msgSeq;

    // @Id
    // 채팅방SEQ
    private Long roomSeq;
    
    // 보낸유형 (FIRST/OTHER)
    private SendType sendType;

    // 보낸사람 (user/assistant)
    private Sender sender;

    // 메시지내용
    private String content;

    // 메시지순서
    private int sequence;

    // 등록일시
    private String regDt;

    @Builder
    public ChatMsg(Long msgSeq, Long roomSeq, SendType sendType, Sender sender, String content, int sequence, String regDt) {

        this.msgSeq = msgSeq;
        this.roomSeq = roomSeq;
        this.sendType = sendType;
        this.sender = sender;
        this.content = content;
        this.sequence = sequence;
        this.regDt = regDt;
    }
}
