package com.example.resourceserverjwt;

import com.example.resourceserverjwt.web.filter.LoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ResourceServerJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceServerJwtApplication.class, args);
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

}
