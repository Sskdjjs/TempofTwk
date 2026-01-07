package com.example.demo.DTO;
// UserVO.java

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
}
