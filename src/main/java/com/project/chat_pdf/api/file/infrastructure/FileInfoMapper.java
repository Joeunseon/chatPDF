package com.project.chat_pdf.api.file.infrastructure;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.file.application.dto.FileDTO;
import com.project.chat_pdf.api.file.domain.FileInfo;

@Mapper
public interface FileInfoMapper {

    public FileDTO findById(Long fileSeq);

    public Long create(FileInfo file);
}
