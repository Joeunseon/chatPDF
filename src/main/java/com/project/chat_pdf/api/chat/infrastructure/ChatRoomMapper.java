package com.project.chat_pdf.api.chat.infrastructure;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.chat.domain.ChatRoom;

@Mapper
public interface ChatRoomMapper {

    public Long create(ChatRoom chatRoom);
}
