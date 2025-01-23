package com.project.chat_pdf.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@Configuration
public class CustomErrorPageConfig {

    @Bean
    public WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> webServerFactoryCustomizer() {
        return factory -> {
            factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/error/404"));
            factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error/500"));
            factory.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/error/default"));
            factory.addErrorPages(new ErrorPage(RuntimeException.class, "/error/default"));
        };
    }
}
