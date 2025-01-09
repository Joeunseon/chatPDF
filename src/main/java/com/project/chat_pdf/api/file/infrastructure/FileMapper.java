package com.project.chat_pdf.api.file.infrastructure;

import org.apache.ibatis.annotations.Mapper;

import com.project.chat_pdf.api.file.application.dto.FileDTO;
import com.project.chat_pdf.api.file.domain.File;

@Mapper
public interface FileMapper {

    public FileDTO findById(Long fileSeq);

    public Long create(File file);
}
