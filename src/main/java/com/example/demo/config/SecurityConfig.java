package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
@EnableWebSecurity
public class SecurityConfig {
//    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
//implements WebMvcConfigurer

    //   密码编码器
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
        // 可以指定强度（默认10，范围4-31）
        // return new BCryptPasswordEncoder(12);
    }
    // 🔥 关键：配置安全过滤器链
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF（REST API 不需要）
                .csrf(csrf -> csrf.disable())

                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 允许所有请求（根据你的需求调整）
                        .anyRequest().permitAll()  // ⬅️ 这行允许所有接口
                );

        return http.build();
    }
}