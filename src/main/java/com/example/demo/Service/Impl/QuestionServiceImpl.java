package com.example.demo.Service.Impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.demo.DTO.request.CreateQuestionRequest;
import com.example.demo.DTO.request.QuestionQueryRequest;
import com.example.demo.DTO.request.UpdateQuestionRequest;
import com.example.demo.DTO.response.PageResult;
import com.example.demo.DTO.response.QuestionVO;
import com.example.demo.DTO.response.TagVO;
import com.example.demo.DTO.response.UserSimpleVO;
import com.example.demo.Service.QuestionService;
import com.example.demo.Service.TagService;
import com.example.demo.entity.Question;
import com.example.demo.entity.Tag;
import com.example.demo.entity.User;
import com.example.demo.mapper.QuestionMapper;
import com.example.demo.mapper.QuestionTagMapper;
import com.example.demo.mapper.TagMapper;
import com.example.demo.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor

public class QuestionServiceImpl implements QuestionService {

    private final QuestionMapper questionMapper;
    private final TagService tagService;
    private final UserMapper userMapper;
    private final QuestionTagMapper questionTagMapper;
    private final TagMapper tagMapper;
    @Override
    @Transactional
    public QuestionVO createQuestion(CreateQuestionRequest request, Long userId) {
        log.info("创建问题: userId={}, title={}", userId, request.getTitle());

//        // 1. 创建问题实体
//        Question question = new Question();
//        BeanUtil.copyProperties(request, question);
//        question.setUserId(userId);

        Question question = new Question();
        question.setUserId(userId);
        question.setTitle(request.getTitle());
        question.setContent(request.getContent());
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setVoteCount(0);
        question.setStatus(1);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        // 2. 保存问题
        questionMapper.insert(question);
        log.info("问题创建成功: questionId={}", question.getId());

//        // 3. 处理标签
//        if (request.getTags() != null && !request.getTags().isEmpty()) {
//            tagService.processTags(request.getTags());
//            // 这里还需要关联问题和标签（需要实现question_tag表操作）
//        }
        // 3. 处理标签（传入问题ID）
        List<TagVO> tagVOs = null;
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            tagVOs = tagService.processTags(request.getTags(), question.getId());
            log.info("处理了 {} 个标签: {}", tagVOs.size(),
                    tagVOs.stream().map(TagVO::getName).toList());
        }

        // 4. 返回VO
        QuestionVO vo = convertToVO(question);
        vo.setTags(tagVOs);
        return vo;
    }

    @Override
    @Transactional
    public QuestionVO updateQuestion(Long questionId, UpdateQuestionRequest request, Long userId) {
        // 1. 验证并获取现有问题
        Question existing = questionMapper.selectById(questionId);
        if (existing == null) throw new RuntimeException("问题不存在");
        if (!existing.getUserId().equals(userId)) throw new RuntimeException("无权限");
        // 2. 创建完全新的Question对象
        Question updated = Question.builder()
                .id(existing.getId())
                .userId(existing.getUserId())
                .title(getUpdatedValue(request.getTitle(), existing.getTitle()))
                .content(getUpdatedValue(request.getContent(), existing.getContent()))
                .status(getUpdatedValue(request.getStatus(), existing.getStatus()))
                .viewCount(existing.getViewCount())
                .answerCount(existing.getAnswerCount())
                .voteCount(existing.getVoteCount())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();
        // 3. 验证业务规则
        if (updated.getStatus() != 1 && updated.getStatus() != 2) {
            throw new RuntimeException("状态值无效");
        }

        // 4. 执行更新
        questionMapper.updateById(updated);
        // 5. 处理标签
        List<TagVO> tags = request.getTags() != null ?
                updateQuestionTags(questionId, request.getTags()) :
                tagService.getTagsByQuestionId(questionId);

        // 6. 返回结果
        QuestionVO vo = convertToVO(updated);
        vo.setTags(tags);
        return vo;
    }
    // 辅助方法：获取更新值或保持原值
    private <T> T getUpdatedValue(T newValue, T defaultValue) {
        return newValue != null ? newValue : defaultValue;
    }

    // 更新标签
    private List<TagVO> updateQuestionTags(Long questionId, List<String> newTags) {
        tagService.removeQuestionTags(questionId);
        return newTags.isEmpty() ?
                Collections.emptyList() :
                tagService.processTags(newTags, questionId);
    }

    @Override
    public boolean deleteQuestion(Long questionId, Long userId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null || !question.getUserId().equals(userId)) {
            return false;
        }

        // 1. 删除标签关联
        tagService.removeQuestionTags(questionId);

        // 2. 软删除问题（修改状态）
        question.setStatus(3);  // 3表示删除
        questionMapper.updateById(question);

        return true;
//        return false;
    }

    @Override
    public QuestionVO getQuestionById(Long questionId) {
        log.info("获取问题详情: questionId={}", questionId);

        // 1. 查询问题
        Question question = questionMapper.selectById(questionId);
        if (question == null || question.getStatus() == 3) {
            throw new RuntimeException("问题不存在或已被删除");
        }

        // 2. 增加浏览数
        increaseViewCount(questionId);

        // 3. 查询关联的用户信息和标签
        // ... 这里需要实现关联查询
        QuestionVO vo = new QuestionVO();
        // 复制基本属性（最安全的方式，一个字段一个字段复制）
        vo.setId(question.getId());
        vo.setTitle(question.getTitle());
        vo.setContent(question.getContent());
        vo.setViewCount(question.getViewCount());
//        BeanUtil.copyProperties(question,vo);
        User author = userMapper.selectById(question.getId());
        if (author  != null) {
            UserSimpleVO userVO = new UserSimpleVO();
            userVO.setId(author.getId());
            userVO.setUsername(author.getUsername());
            vo.setAuthor(userVO);
            /**
             * 以下的先不管
             * 大概是关于问题作者信息后面完善
             */
//            userVO.setAvatar(author.getAvatar());
//
//            // 如果有其他字段
//            userVO.setTitle(author.getTitle());
//            userVO.setLevel(author.getLevel());

//            BeanUtil.copyProperties(author,userVO);
//            vo.setAuthor(userVO);
        }
        // 5. 查询并设置标签信息
        List<Long> tags = questionTagMapper.selectTagIdsByQuestionId(question.getId());

        // 创建 TagVO 列表
        List<TagVO> tagVOList = new ArrayList<>();
        // 遍历转换
        for (Long tag : tags) {
            TagVO tagVO = new TagVO();
            tagVO.setId(tag);
            Tag a = new Tag();
             a = tagMapper.selectById(tag);
            tagVO.setName(a.getName());
            // 如果 Tag 实体有其他字段，可以在这里添加
            tagVO.setQuestionCount(a.getQuestionCount());
            tagVOList.add(tagVO);
        }
        vo.setTags(tagVOList);
//        // 设置标签
//        List<Tag> tags = tagMapper.selectTagsByQuestionId(question.getId());
//        List<String> tagNames = tags.stream()
//                .map(Tag::getName)
//                .collect(Collectors.toList());
//        vo.setTags(tagNames);

        // 设置统计信息
        vo.setAnswerCount(2);
//        answerMapper.countByQuestionId(question.getId())
        vo.setViewCount(question.getViewCount()); // 从question实体获取

        return vo;
//        return convertToVO(question);
    }

    @Override
    public PageResult<QuestionVO> queryQuestions(QuestionQueryRequest request) {
        log.info("查询问题列表");

        // 1. 构建查询条件
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);  // 只查询正常状态的问题

        if (request.getUserId() != null) {
            wrapper.eq("user_id", request.getUserId());
        }

        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {

            wrapper.and(w -> w.like("title", request.getKeyword())
                    .or()
                    .like("content", request.getKeyword()));

//            // 先去掉关键词两边的空格
//            String keyword = request.getKeyword().trim();
//
//            // 创建子查询条件
//            QueryWrapper<Question> subWrapper = new QueryWrapper<>();
//
//            // 标题包含关键词
//            subWrapper.like("title", keyword);
//
//            // 或者 内容包含关键词
//            subWrapper.or().like("content", keyword);
//
//            // 把这个子条件添加到主查询中
//            wrapper.and(subWrapper);
        }

        // 2. 排序
        String orderBy = request.getOrderBy();
        if ("voteCount".equals(orderBy)) {
            wrapper.orderBy(true, !request.getDesc(), "vote_count");
        } else if ("answerCount".equals(orderBy)) {
            wrapper.orderBy(true, !request.getDesc(), "answer_count");
        } else {
            wrapper.orderBy(true, !request.getDesc(), "created_at");
        }

        // 3. 分页查询
        Page<Question> page = new Page<>(request.getPage(), request.getSize());
        Page<Question> questionPage = questionMapper.selectPage(page, wrapper);

        // 4. 转换为VO列表（新手写法）
        List<Question> questions = questionPage.getRecords();  // 获取查询结果
        List<QuestionVO> questionVOs = new ArrayList<>();      // 创建空列表

        // 遍历每个问题并转换
        for (Question question : questions) {
            QuestionVO vo = convertToVO(question);
            questionVOs.add(vo);
        }

        // 5. 返回分页结果
        return new PageResult<>(
                questionVOs,
                questionPage.getCurrent(),
                questionPage.getSize(),
                questionPage.getTotal()
        );
    }

    @Override
    public PageResult<QuestionVO> getUserQuestions(Long userId, Integer page, Integer size) {
        return null;
    }

    @Override
    public void increaseViewCount(Long questionId) {
        // 简单实现：直接更新
        Question question = questionMapper.selectById(questionId);
        if (question != null) {
            question.setViewCount(question.getViewCount() + 1);
            questionMapper.updateById(question);
        }
    }

    /**
     * 转换为VO（简化版）
     */
    private QuestionVO convertToVO(Question question) {
        QuestionVO vo = new QuestionVO();
        BeanUtil.copyProperties(question, vo);
        return vo;
    }
}
