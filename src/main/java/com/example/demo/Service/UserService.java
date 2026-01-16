package com.example.demo.Service;

// UserService.java


import com.example.demo.DTO.request.LoginRequest;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.UserVO;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {

    //    private  final UserMapper userMapper;
    String test();

    /**
     * 用户注册
     */
    UserVO register(RegisterRequest request);

    /**
     * 用户登录
     */
    LoginResponse login(LoginRequest request);

    /**
     * 根据用户名获取用户信息
     */
    UserVO getUserByUsername(String username);

    Long getCurrentUserId(HttpServletRequest request);
}