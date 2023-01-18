package com.ll.exam.conong.base.security;

import com.ll.exam.conong.bounded_context.account.adapter.in.http.CustomAuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final CustomAuthSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .logout(logout -> logout
                        .logoutUrl("/account/logout")
                )
                .oauth2Login(
                        oauth2Login -> oauth2Login
                                .successHandler(authenticationSuccessHandler)
                                .loginPage("/account/login")
                );

        return http.build();
    }
}
