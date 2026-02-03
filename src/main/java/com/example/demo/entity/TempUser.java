package com.example.demo.entity;

// src/main/java/com/example/redisdemo/entity/User.java
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempUser implements Serializable {
    private Long id;
    private String name;
    private Integer age;
    private String email;

    // 快捷创建方法
    public static TempUser createSample(Long id) {
        return new TempUser(id, "用户" + id, 20 + id.intValue(),
                "user" + id + "@example.com");
    }
}
