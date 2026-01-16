package com.example.demo.DTO.response;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteDTO {
    @NotNull(message = "投票类型不能为空")
    @Min(-1) @Max(1)
    private Integer voteType; // 1:赞, -1:踩
}