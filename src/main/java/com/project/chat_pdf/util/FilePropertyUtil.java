package com.project.chat_pdf.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilePropertyUtil {

    @Value("${spring.servlet.multipart.maxFileSize}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.maxRequestSize}")
    private String maxRequestSize;

    public String getMaxFileSize() {
        return maxFileSize;
    }

    public String getMaxRequestSize() {
        return maxRequestSize;
    }
}
