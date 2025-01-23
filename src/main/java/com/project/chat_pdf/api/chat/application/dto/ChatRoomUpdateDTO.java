package com.project.chat_pdf.api.chat.application.dto;

import com.project.chat_pdf.api.chat.domain.ChatRoom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUpdateDTO {

    private Long roomSeq;

    private String title;

    public ChatRoom toEntitiy() {
        return ChatRoom.builder()
                .roomSeq(roomSeq)
                .title(title)
                .build();
    }
}
