package com.example.demo.DTO.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAnswerDTO {
    @NotBlank(message = "回答内容不能为空")
    @Size(min = 10, message = "回答内容至少10个字")
    private String content;
}