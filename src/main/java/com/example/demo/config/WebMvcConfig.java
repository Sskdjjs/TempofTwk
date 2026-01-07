package com.example.demo.config;


import com.example.demo.interceptor.JwtAuthenticationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")  // 保护所有API
                .excludePathPatterns(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/users/health",
                        "/api/hello",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}