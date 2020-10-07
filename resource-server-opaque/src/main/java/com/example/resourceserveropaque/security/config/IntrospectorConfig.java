package com.example.resourceserveropaque.security.config;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class IntrospectorConfig {

    // タイムアウトとBasic認証が設定済みのRestTemplateを利用するNimbusOpaqueTokenIntrospectorを定義
    @Bean
    public NimbusOpaqueTokenIntrospector opaqueTokenIntrospector(RestTemplateBuilder builder,
                                                                 OAuth2ResourceServerProperties properties) {
        OAuth2ResourceServerProperties.Opaquetoken opaquetoken = properties.getOpaquetoken();
        RestTemplate restTemplate = builder.setReadTimeout(Duration.ofMillis(500))
                .setConnectTimeout(Duration.ofMillis(500))
                .basicAuthentication(opaquetoken.getClientId(), opaquetoken.getClientSecret())
                .build();
        return new NimbusOpaqueTokenIntrospector(opaquetoken.getIntrospectionUri(), restTemplate);
    }
}
