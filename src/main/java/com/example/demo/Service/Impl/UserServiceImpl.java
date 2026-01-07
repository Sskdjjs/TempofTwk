package com.example.demo.Service.Impl;

// UserServiceImpl.java

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.DTO.request.LoginRequest;
import com.example.demo.DTO.response.LoginResponse;
import com.example.demo.DTO.request.RegisterRequest;
import com.example.demo.DTO.UserVO;
import com.example.demo.Service.UserService;
import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private  final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Override
    public String test() {
    log.info("UserService 测试方法");
    return "UserService is working!";
    }
    @Override
    @Transactional
    public UserVO register(RegisterRequest request) {
//         1. 检查用户名是否已存在
        if (userMapper.countByUsername(request.getUsername()) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 2. 检查邮箱是否已存在
        if (userMapper.countByEmail(request.getEmail()) > 0) {
            throw new RuntimeException("邮箱已注册");
        }

        // 3. 创建用户（简化：密码未加密）
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(request.getPassword());
        String encodePassword = passwordEncoder.encode(request.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setPasswordHash(encodePassword); // 注意：生产环境必须加密！

        // 4. 保存到数据库
        userMapper.insert(user);

        // 5. 返回用户信息（不包含密码）
        return convertToVO(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 1. 根据用户名查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

//         2. 验证密码（简化：明文比较）
        boolean match = passwordEncoder.matches(request.getPassword(),user.getPasswordHash());
        if (!match) {
            throw new RuntimeException("密码错误");
        }
//        boolean a = user.getPasswordHash().equals(request.getPassword());
//        if (!a) {
//            throw new RuntimeException("密码错误");
//        }
        String token = jwtUtil.generateToken(user.getId(),user.getUsername());
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn((long) (24*60*60))
                .userId(user.getId())
                .username(user.getUsername())
                .build();

        // 3. 返回用户信息
//        return convertToVO(user);
    }

    @Override
    public UserVO getUserByUsername(String username) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return convertToVO(user);
    }

    /**
     * 将User实体转换为UserVO（移除敏感信息）
     */
    private UserVO convertToVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        BeanUtil.copyProperties(user, vo, "passwordHash");
        return vo;
    }
}
