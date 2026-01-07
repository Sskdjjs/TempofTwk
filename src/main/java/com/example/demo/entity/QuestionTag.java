package com.example.demo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("question_tag")
public class QuestionTag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long questionId;

    private Long tagId;

    private LocalDateTime createdAt;
}