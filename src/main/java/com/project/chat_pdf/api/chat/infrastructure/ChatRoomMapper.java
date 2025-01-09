package com.project.chat_pdf.api.chat.infrastructure;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.chat.application.dto.ChatRoomDTO;
import com.project.chat_pdf.api.chat.domain.ChatRoom;

@Mapper
public interface ChatRoomMapper {

    public Integer countAll();

    public List<ChatRoomDTO> findAll();

    public Long create(ChatRoom chatRoom);
}
