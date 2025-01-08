package com.project.chat_pdf.api.chat.infrastructure;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.chat.domain.ChatMsg;

@Mapper
public interface ChatMsgMapper {

    public Integer findNextOrder(Long roomSeq);

    public Long create(ChatMsg chatMsg);

}
