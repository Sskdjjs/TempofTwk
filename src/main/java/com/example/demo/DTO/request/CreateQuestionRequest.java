package com.example.demo.DTO.request;

// CreateQuestionRequest.java

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class CreateQuestionRequest {

    @NotBlank(message = "标题不能为空")
    @Size(min = 5, max = 200, message = "标题长度5-200字")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(min = 10, message = "内容至少10字")
    private String content;

    private List<String> tags;  // 标签名列表
}



