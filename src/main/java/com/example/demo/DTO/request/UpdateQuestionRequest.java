package com.example.demo.DTO.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

// UpdateQuestionRequest.java
@Data
public class UpdateQuestionRequest {

    @Size(min = 5, max = 200, message = "标题长度5-200字")
    private String title;

    @Size(min = 10, message = "内容至少10字")
    private String content;

    private List<String> tags;

    private Integer status;  // 1-正常 2-关闭
}

