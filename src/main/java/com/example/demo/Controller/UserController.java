package com.example.demo.Controller;

// UserController.java

import com.example.demo.DTO.*;
import com.example.demo.DTO.request.LoginRequest;
import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.response.ApiResponse;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.Service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
//import javax.validation.Valid;
import jakarta.validation.Valid;
@CrossOrigin(origins = "*")  // 允许所有前端访问
@Slf4j
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private  final UserService userService;
    @GetMapping("/test")
    public String test() {
        log.info("测试接口被调用");
        return userService.test();
    }


    /**
     * 用户注册
     * POST http://localhost:8080/api/users/register
//     */
    @PostMapping("/register")
    public ApiResponse<UserVO> register(@Valid @RequestBody RegisterRequest request) {
        log.info("用户注册: {}", request.getUsername());
        try {
            UserVO userVO = userService.register(request);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 用户登录
     * POST http://localhost:8080/api/users/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("用户登录: {}", request.getUsername());
        try {
            LoginResponse loginResponse = userService.login(request);
            return ApiResponse.success(loginResponse);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     * GET http://localhost:8080/api/users/{username}
     */
    @GetMapping("/{username}")
    public ApiResponse<UserVO> getUser(@PathVariable String username) {
        try {
            UserVO userVO = userService.getUserByUsername(username);
            return ApiResponse.success(userVO);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 健康检查
     * GET http://localhost:8080/api/users/health
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("用户服务运行正常");
    }
}
