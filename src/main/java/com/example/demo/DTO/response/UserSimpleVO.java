package com.example.demo.DTO.response;

import lombok.Data;

@Data
public class UserSimpleVO {

    // 必须包含的字段
    private Long id;           // 用户ID
    private String username;   // 用户名/昵称
    private String avatar;     // 头像URL

    // 可选的常用字段（根据你的需求添加）
    private String title;      // 用户头衔/称号（如"资深开发者"）
    private Integer level;     // 用户等级
    private String signature;  // 个人签名/简介
    private Integer answerCount; // 回答数量（如果需要显示）
    private Integer questionCount; // 提问数量
}