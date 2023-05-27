package com.project.instagram.config.env;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("local")
public class LocalConfiguration implements EnvConfiguration{

    @Value("${custom.loading.message}")
    private String message;

    @Override
    @Bean
    public String getMessage() {
        log.info("[getMessage] LocalConfiguration 입니다.");
        return message;
    }
}
