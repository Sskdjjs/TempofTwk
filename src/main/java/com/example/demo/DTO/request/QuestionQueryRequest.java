package com.example.demo.DTO.request;

import lombok.Data;

// QuestionQueryRequest.java
@Data
public class QuestionQueryRequest {

    private Integer page = 1;

    private Integer size = 20;

    private String keyword;  // 搜索关键词

    private Long tagId;      // 按标签筛选

    private Long userId;     // 按用户筛选

    private String orderBy = "createdAt";  // 排序字段：createdAt, voteCount, answerCount

    private Boolean desc = true;  // 是否降序
}