package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.AnswerVote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AnswerVoteMapper extends BaseMapper<AnswerVote> {
    @Select("SELECT COUNT(*) FROM answer_votes WHERE answer_id = #{answerId} AND vote_type = 1")
    Integer countUpvotes(Long answerId);

    @Select("SELECT COUNT(*) FROM answer_votes WHERE answer_id = #{answerId} AND vote_type = -1")
    Integer countDownvotes(Long answerId);
}