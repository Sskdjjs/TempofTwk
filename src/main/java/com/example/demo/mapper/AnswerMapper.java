package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Answer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {
    // 复杂查询可以后面加
}
