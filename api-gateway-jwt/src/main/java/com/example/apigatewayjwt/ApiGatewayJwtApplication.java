package com.example.apigatewayjwt;

import com.example.apigatewayjwt.web.filter.LoggingFilter;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.oauth2.server.resource.web.reactive.function.client.ServletBearerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SpringBootApplication
@EnableConfigurationProperties(WebClientProperties.class)
public class ApiGatewayJwtApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayJwtApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayJwtApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean loggingFilter() {
        LoggingFilter loggingFilter = new LoggingFilter();
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>(loggingFilter);
        // フィルターの順番を一番最初に指定
        registrationBean.setOrder(Integer.MIN_VALUE);
        // url-patternを指定
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder,
                               @Value("${resource-server.uri}") String resourceServerUri,
                               WebClientProperties webClientProperties) {
        // タイムアウトを設定
        Function<? super TcpClient, ? extends TcpClient> tcpMapper = tcpClient -> {
            // Connect Timeoutを500msに設定
            return tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                    webClientProperties.getConnectTimeout())
                    .doOnConnected(conn -> conn
                            // Read Timeoutを500msに設定
                            .addHandlerLast(new ReadTimeoutHandler(
                                    webClientProperties.getReadTimeout(),
                                    TimeUnit.MILLISECONDS))
                            // Write Timeoutを500msに設定
                            .addHandlerLast(new WriteTimeoutHandler(
                                    webClientProperties.getWriteTimeout(),
                                    TimeUnit.MILLISECONDS))
                    );
        };
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpMapper);
        // OAuth2関連の設定
        return builder.baseUrl(resourceServerUri)
                // 作成したHttpClientを追加
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                // "Accept: application/json"をデフォルトでリクエストヘッダーに出力する
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // Bearer Token Propagationの設定
                .filter(new ServletBearerExchangeFilterFunction())
                // WebClientからのリクエスト送信時に、ヘッダーをログ出力する
                .filter((request, next) -> {
                    logger.debug("~~[Sending request]~~~~~~~~~~~~~~~~~~~~~");
                    logger.debug("{} {}", request.method(), request.url());
                    for (Map.Entry<String, List<String>> entry : request.headers().entrySet()) {
                        logger.debug("{}: {}", entry.getKey(), entry.getValue());
                    }
                    logger.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    return next.exchange(request);
                })
                .build();
    }

}
