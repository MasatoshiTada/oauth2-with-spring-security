package com.example.resourceserveropaque.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    OpaqueTokenIntrospector introspector;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.GET, "/todos/**").hasAuthority("SCOPE_todo:read")
                .mvcMatchers("/todos/**").hasAuthority("SCOPE_todo:write")
                .anyRequest().authenticated();
        http.oauth2ResourceServer()
                .opaqueToken()
                .introspector(introspector);
    }

}
