package com.example.demo.DTO.response;

import lombok.Data;

// TagVO.java
@Data
public class TagVO {

    private Long id;

    private String name;

    private String description;

    private Integer questionCount;
}