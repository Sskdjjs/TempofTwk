package com.example.demo.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.demo.DTO.response.AnswerVO;
import com.example.demo.Service.AnswerService;
import com.example.demo.entity.Answer;
import com.example.demo.entity.Question;
import com.example.demo.mapper.AnswerMapper;
import com.example.demo.mapper.QuestionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {
    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    @Override
    public AnswerVO createAnswer(Long questionId, Long userId, String content) {
        Answer answer = Answer.builder()
                .questionId(questionId)
                .userId(userId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        answerMapper.insert(answer);
        questionMapper.update(null,
                new UpdateWrapper<Question>()
                .eq("id",questionId)
                .setSql("answer_count = answer_count + 1"));
        return convertToVO(answer,userId);
    }

    /**
     * 未实现分页查询
     * @param questionId
     * @param currentUserId
     * @return
     */
    @Override
    public List<AnswerVO> getAnswersByQuestionId(Long questionId, Long currentUserId) {
//        answerMapper.selectList(new QueryWrapper<Answer>()
//                        .eq("question_id",questionId)
//                        .eq("is_deleted",false)
//                        .orderByDesc("created_at")
//                );

        List<Answer> list = answerMapper.selectList(new LambdaQueryWrapper<Answer>()
                .eq(Answer::getQuestionId,questionId)
                .eq(Answer::getIsDeleted,false)
                .orderByDesc(Answer::getCreatedAt));
        List<AnswerVO> answerVOList = new ArrayList<>();
//        for (int i = 0;i<list.size();i++) {
//            AnswerVO answerVO = new AnswerVO();
//            BeanUtil.copyProperties(list.get(i),answerVO);
//            answerVOList.add(answerVO);
//        }
        for (Answer answer : list) {
            AnswerVO answerVO = new AnswerVO();
            BeanUtil.copyProperties(answer,answerVO);
            answerVOList.add(answerVO);
        }
        return answerVOList;
    }

    @Override
    public void deleteAnswer(Long answerId, Long userId) {

    }

    @Override
    public void voteAnswer(Long answerId, Long userId, Integer voteType) {

    }

    @Override
    public void updateVoteCount(Long answerId) {

    }

    @Override
    public AnswerVO convertToVO(Answer answer, Long currentUserId) {
        AnswerVO answerVO = new AnswerVO();
        BeanUtil.copyProperties(answer,answerVO);
        return answerVO;
    }
}
