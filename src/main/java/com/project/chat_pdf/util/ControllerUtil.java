package com.project.chat_pdf.util;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ControllerUtil {

    @FunctionalInterface
    public interface RequestHandler<T> {
        T process() throws Exception;
    }

    /**
     * 공통 요청 처리
     * @param handler 처리할 로직
     * @param <T> 반환 타입
     * @return ResponseEntity<T>
     */
    public static <T> ResponseEntity<T> handleRequest(RequestHandler<T> handler) {
        try {
            T response = handler.process();
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid request: ", e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Internal server error: ", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * 유효성 검사
     * @param paramName 매개변수 이름
     * @param value 매개변수 값
     * @param allowedValues 허용되는 값 목록
     */
    public static void validateParams(String paramName, String value, String... allowedValues) {
        if (StringUtils.isBlank(value)) {
            throw new IllegalArgumentException(paramName + " cannot be blank");
        }
        if (allowedValues.length > 0 && !Arrays.asList(allowedValues).contains(value)) {
            throw new IllegalArgumentException(paramName + " must be one of: " + Arrays.toString(allowedValues));
        }
    }
}
