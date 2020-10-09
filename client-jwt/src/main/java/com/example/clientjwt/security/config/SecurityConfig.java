package com.example.clientjwt.security.config;

import com.example.clientjwt.security.keycloak.KeycloakService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.DefaultAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final KeycloakService keycloakService;
    private final RestTemplateBuilder restTemplateBuilder;

    public SecurityConfig(KeycloakService keycloakService, RestTemplateBuilder restTemplateBuilder) {
        this.keycloakService = keycloakService;
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // /css/**, /js/**, /images/**, /webjars/**, /**/favicon.ico は􏰁セキュリティ保護対象外
        web.ignoring().requestMatchers(
                PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.oauth2Login(oauth2 -> oauth2
                .tokenEndpoint(token -> token
                        // タイムアウト設定済みのDefaultAuthorizationCodeTokenResponseClientを指定
                        .accessTokenResponseClient(accessTokenResponseClient())
                ).userInfoEndpoint(userInfo -> userInfo
                        // タイムアウト設定済みのDefaultOAuth2UserServiceを指定
                        .userService(oAuth2UserService())
                        // タイムアウト設定済みのOidcUserServiceを指定
                        .oidcUserService(oidcUserService())
                ).loginPage("/login")
                .permitAll()
        ).authorizeRequests(auth -> auth
                .anyRequest().authenticated()
        ).logout(logout -> logout
                // 認可サーバーからもログアウトする
                .addLogoutHandler(logoutFromAuthServer())
                .invalidateHttpSession(true)
                .permitAll()
        );
    }

    private DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient() {
        RestTemplate restTemplate = restTemplateBuilder
                // タイムアウトを設定
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(1000))
                // トークンエンドポイントからのレスポンスを解析するためのHttpMessageConverterを設定
                .messageConverters(
                        new FormHttpMessageConverter(),
                        new OAuth2AccessTokenResponseHttpMessageConverter())
                // トークンエンドポイントからのエラーレスポンスを解析するためのErrorHandlerを設定
                .errorHandler(new OAuth2ErrorResponseErrorHandler())
                .build();
        DefaultAuthorizationCodeTokenResponseClient accessTokenResponseClient =
                new DefaultAuthorizationCodeTokenResponseClient();
        // タイムアウト設定済みのRestTemplateを設定
        accessTokenResponseClient.setRestOperations(restTemplate);
        return accessTokenResponseClient;
    }

    private DefaultOAuth2UserService oAuth2UserService() {
        RestTemplate restTemplate = restTemplateBuilder
                // タイムアウトを設定
                .setConnectTimeout(Duration.ofMillis(1000))
                .setReadTimeout(Duration.ofMillis(1000))
                // /userinfoなどからのエラーレスポンスを解析するためのErrorHandlerを設定
                .errorHandler(new OAuth2ErrorResponseErrorHandler())
                .build();
        DefaultOAuth2UserService oAuth2UserService = new DefaultOAuth2UserService();
        // タイムアウト設定済みのRestTemplateを設定
        oAuth2UserService.setRestOperations(restTemplate);
        return oAuth2UserService;
    }

    private OidcUserService oidcUserService() {
        OidcUserService oidcUserService = new OidcUserService();
        // タイムアウト設定済みのOAuth2UserServiceを設定
        oidcUserService.setOauth2UserService(oAuth2UserService());
        return oidcUserService;
    }

    /**
     * 認可サーバーからログアウトするLogoutHandler
     */
    private LogoutHandler logoutFromAuthServer() {
        return (request, response, authentication) -> {
            keycloakService.logout();
        };
    }

}
