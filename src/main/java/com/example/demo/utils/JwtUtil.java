package com.example.demo.utils;


import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Component
public class JwtUtil {

    @Value("${jwt.secret:your-secret-key-change-in-production}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration; // 默认24小时，单位毫秒

//    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    }
    // 生成密钥
    private SecretKey getSigningKey() {
        // 密钥长度至少32位
        String safeSecret = secret.length() < 32 ?
                secret + "0".repeat(32 - secret.length()) :
                secret.substring(0, 32);
        return Keys.hmacShaKeyFor(safeSecret.getBytes(StandardCharsets.UTF_8));
    }
//    /**
//     * 生成JWT Token
//     */
//    public String generateToken(Long userId, String username) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("userId", userId);
//        claims.put("username", username);
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
//                .compact();
//    }
    /**
     * 生成JWT Token（最简单用法）
     */
    public String generateToken(Long userId) {
        return generateToken(userId, null);
    }
    /**
     * 生成JWT Token（带用户名）
     */
    public String generateToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        if (username != null) {
            claims.put("username", username);
        }

        return Jwts.builder()
                .setClaims(claims)  // 自定义数据
                .setIssuedAt(new Date())  // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))  // 过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)  // 签名算法
                .compact();  // 生成字符串
    }

    /**
     * 验证Token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token已过期: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token格式错误: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Token验证失败: {}", e.getMessage());
        }
        return false;
    }
    /**
     * 从Token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 从Token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("username", String.class);
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 刷新Token（延长过期时间）
     */
    public String refreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 使用原claims生成新token（延长过期时间）
            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            return null;
        }
    }
}