package com.example.demo.entity;


import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("question")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;            // 提问者ID

    private String title;           // 问题标题

    private String content;         // 问题内容（支持Markdown）

    private Integer viewCount = 0;  // 浏览数

    private Integer answerCount = 0; // 回答数

    private Integer voteCount = 0;   // 投票数

    private Integer status = 1;     // 状态：1-正常 2-关闭 3-删除

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // 非数据库字段：关联的用户信息
    @TableField(exist = false)
    private User user;

    // 非数据库字段：标签列表
    @TableField(exist = false)
    private List<Tag> tags;
}