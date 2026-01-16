package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("answer_votes")
public class AnswerVote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long answerId;
    private Long userId;
    private Integer voteType; // 1:赞, -1:踩
    private LocalDateTime createdAt;
}