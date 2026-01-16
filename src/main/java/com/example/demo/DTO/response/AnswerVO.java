package com.example.demo.DTO.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerVO {
    private Long id;
    private Long questionId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private String content;
    private Integer upvoteCount;
    private Integer downvoteCount;
    private Integer commentCount;
    private Boolean isAccepted;
    private LocalDateTime createdAt;
    private Integer userVoteType; // 当前用户投票状态：null, 1, -1
}