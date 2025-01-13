package com.project.chat_pdf.api.chat.application.dto;

import com.project.chat_pdf.api.chat.value.SendType;
import com.project.chat_pdf.api.chat.value.Sender;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMsgDTO {
    
    // 보낸 유형 (FIRST/OTHER)
    private SendType sendType;

    // 보낸사람 (user/assistant)
    private Sender sender;

    // 메시지내용
    private String content;

    // 메시지순서
    private int sequence;
}
