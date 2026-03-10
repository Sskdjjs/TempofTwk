package com.example.demo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("answer_votes")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVote {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long answerId;
    private Long userId;
    private Integer voteType; // 1:赞, -1:踩 0:取消
    private LocalDateTime createdAt;
}