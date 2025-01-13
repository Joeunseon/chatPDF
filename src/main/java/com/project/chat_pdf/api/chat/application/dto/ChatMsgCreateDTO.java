package com.project.chat_pdf.api.chat.application.dto;

import com.project.chat_pdf.api.chat.domain.ChatMsg;
import com.project.chat_pdf.api.chat.value.SendType;
import com.project.chat_pdf.api.chat.value.Sender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsgCreateDTO {

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

    private String apiId;

    public ChatMsg toEntity(String content, Sender sender, int sequence) {
        return ChatMsg.builder()
                .roomSeq(roomSeq)
                .sendType(sendType)
                .sender(sender)
                .content(content)
                .sequence(sequence)
                .build();
    }
}
