package com.project.chat_pdf.api.chat.value;

import com.project.chat_pdf.common.CodeType;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Sender implements CodeType {

    user("사용자"), assistant("AI");

    private final String description;

    @Override
    public String getCode() {
        return name();
    }
}
