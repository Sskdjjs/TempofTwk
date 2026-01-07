package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    @Select("SELECT * FROM tag WHERE name = #{name}")
    Tag selectByName(@Param("name") String name);

    @Select("UPDATE tag SET question_count = question_count + 1 WHERE id = #{tagId}")
    void increaseQuestionCount(@Param("tagId") Long tagId);

    @Select("UPDATE tag SET question_count = question_count - 1 WHERE id = #{tagId}")
    void decreaseQuestionCount(@Param("tagId") Long tagId);
}
