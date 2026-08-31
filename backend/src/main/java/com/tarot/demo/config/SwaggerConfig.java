package com.tarot.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI 타로 분석 및 쿠폰 시스템 REST API")
                        .description("Kafka 기반 비동기 쿠폰 발급 및 관리 API 문서")
                        .version("v1.0.0"));
    }
}