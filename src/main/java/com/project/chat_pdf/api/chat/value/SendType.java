package com.project.chat_pdf.api.chat.value;

import com.project.chat_pdf.common.CodeType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SendType implements CodeType {

    FIRST("첫번째"), OTHER("다른");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
