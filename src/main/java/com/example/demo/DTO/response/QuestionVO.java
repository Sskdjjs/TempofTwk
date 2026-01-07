package com.example.demo.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionVO {

    private Long id;

    private String title;

    private String content;

    private Integer viewCount;

    private Integer answerCount;

    private Integer voteCount;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /**
     * 这里可能有伏笔,先不管了
     */
    // 用户信息
    private UserSimpleVO author;
    // 标签列表
    private List<TagVO> tags;
}