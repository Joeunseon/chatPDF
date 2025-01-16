package com.project.chat_pdf.config;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JasyptConfig {

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {

        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        // 암호화 키 설정: 환경 변수 또는 시스템 속성에서 읽음
        String encryptionKey = System.getenv("JASYPT_ENCRYPTOR_PASSWORD");
        if ( StringUtils.isBlank(encryptionKey) ) {
            throw new IllegalStateException("JASYPT_ENCRYPTOR_PASSWORD 환경변수가 설정되지 않았습니다.");
        }

        // Jasypt 암호화 기본 설정
        config.setPassword(encryptionKey);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");

        encryptor.setConfig(config);
        log.info("Jasypt StringEncryptor가 성공적으로 설정되었습니다.");

        return encryptor;
    }
}
