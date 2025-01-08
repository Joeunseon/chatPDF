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
    // 채팅SEQ
    private Long chatSeq;

    // @Id
    // 채팅방SEQ
    private Long roomSeq;

    // 보낸사람 (user/assistant)
    private Sender sender;

    // 보낸 유형 (FIRST/OTHER)
    private SendType sendType;

    // 채팅내용
    private String content;

    // 채팅순서
    private int order;

    // 등록일시
    private String regDt;

    private String apiId;

    public ChatMsg toEntity(String content, Sender sender, int order) {
        return ChatMsg.builder()
                .roomSeq(roomSeq)
                .sender(sender)
                .sendType(sendType)
                .content(content)
                .order(order)
                .build();
    }
}
