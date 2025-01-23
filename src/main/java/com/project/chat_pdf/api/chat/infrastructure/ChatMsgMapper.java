package com.project.chat_pdf.api.chat.infrastructure;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.chat.application.dto.ChatMsgDTO;
import com.project.chat_pdf.api.chat.domain.ChatMsg;

@Mapper
public interface ChatMsgMapper {

    public Integer countAll(Long roomSeq);

    public List<ChatMsgDTO> findAll(Long roomSeq);

    public Integer findNextOrder(Long roomSeq);

    public Long create(ChatMsg chatMsg);

}
