package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.QuestionTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionTagMapper extends BaseMapper<QuestionTag> {

    @Select("SELECT tag_id FROM question_tag WHERE question_id = #{questionId}")
    List<Long> selectTagIdsByQuestionId(Long questionId);
}