package com.example.demo.DTO.response;

import lombok.Builder;
import lombok.Data;

// 响应对象
@Data
@Builder
public  class LoginResponse {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
}
