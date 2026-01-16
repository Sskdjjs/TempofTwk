package com.example.demo.interceptor;


import com.example.demo.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
//import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
//    private final RedisUtil redisUtil;
// 公开路径（不需要认证）
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/api/users/register",
        "/api/users/login",
        "/api/users/health",
        "/api/users/{username}", // 查看用户信息（公开）
        "/api/hello",
        "/swagger-ui",
        "/v3/api-docs",
        "/webjars",
        "/swagger-resources"
);

//    @Override
//    public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
//        return HandlerInterceptor.super.preHandle(request, response, handler);
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // 如果不是Controller方法，直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        String requestURI = request.getRequestURI();
        log.info("拦截器处理路径: {}", requestURI);

        // 检查是否为公开路径
        if (isPublicPath(requestURI)) {
            return true;  // 公开路径直接放行
        }

        // 获取Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 设置401响应
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"code\":401,\"message\":\"未提供有效的认证Token\"}");
            } catch (Exception e) {
                log.error("写入响应失败", e);
            }
            return false;
        }

        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        try {
            // 验证Token
            if (!jwtUtil.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"code\":401,\"message\":\"Token无效或已过期\"}");
                return false;
            }

            // 获取用户ID并存入请求属性
            Long userId = jwtUtil.getUserIdFromToken(token);
            request.setAttribute("userId", userId);

            return true;
        } catch (Exception e) {
            log.error("Token验证失败", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"code\":401,\"message\":\"认证失败: " + e.getMessage() + "\"}");
            } catch (Exception ex) {
                // 忽略
            }
            return false;
        }
    }

    private boolean isPublicPath(String requestURI) {
        // 定义不需要认证的公开路径
        return requestURI.startsWith("/api/users/register") ||
                requestURI.startsWith("/api/users/login") ||
                requestURI.startsWith("/api/users/health") ||
                requestURI.equals("/api/hello") ||
                requestURI.startsWith("/swagger-ui") ||
                requestURI.startsWith("/v3/api-docs") ||
                requestURI.startsWith("/webjars") ||
                requestURI.startsWith("/swagger-resources");
    }
}
