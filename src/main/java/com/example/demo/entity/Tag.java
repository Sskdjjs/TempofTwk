package com.example.demo.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tag")
public class Tag implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;           // 标签名

    private String description;    // 标签描述

    private Integer questionCount = 0; // 关联问题数

    private LocalDateTime createdAt;
}
