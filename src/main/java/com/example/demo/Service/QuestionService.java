package com.example.demo.Service;


import com.example.demo.DTO.request.CreateQuestionRequest;
import com.example.demo.DTO.request.QuestionQueryRequest;
import com.example.demo.DTO.request.UpdateQuestionRequest;
import com.example.demo.DTO.response.PageResult;
import com.example.demo.DTO.response.QuestionVO;

import java.util.List;

public interface QuestionService {

    // 创建问题
    QuestionVO createQuestion(CreateQuestionRequest request, Long userId);

    // 更新问题
    QuestionVO updateQuestion(Long questionId, UpdateQuestionRequest request, Long userId);

    // 删除问题（软删除）
    boolean deleteQuestion(Long questionId, Long userId);

    // 获取问题详情
    QuestionVO getQuestionById(Long questionId);

    // 增加浏览数
    void increaseViewCount(Long questionId);

    // 分页查询问题列表
    PageResult<QuestionVO> queryQuestions(QuestionQueryRequest request);

    // 获取用户的问题列表
    PageResult<QuestionVO> getUserQuestions(Long userId, Integer page, Integer size);
}