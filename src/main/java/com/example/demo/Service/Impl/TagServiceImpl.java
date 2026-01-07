package com.example.demo.Service.Impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.example.demo.DTO.response.TagVO;
import com.example.demo.Service.TagService;
import com.example.demo.entity.Question;
import com.example.demo.entity.QuestionTag;
import com.example.demo.entity.Tag;
import com.example.demo.mapper.QuestionTagMapper;
import com.example.demo.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final QuestionTagMapper questionTagMapper;

    @Override
    @Transactional
    public List<TagVO> processTags(List<String> tagNames, Long questionId) {
//        if (CollectionUtils.isEmpty(tagNames)) {
//            return Collections.emptyList();
//        }

        log.info("处理标签: {}, questionId={}", tagNames, questionId);

        List<TagVO> result = new ArrayList<>();

        for (String tagName : tagNames) {
            // 1. 清理标签名（去空格，转小写）
            String cleanName = tagName.trim().toLowerCase();
            if (cleanName.isEmpty()) {
                continue;
            }
/**
 * 忽略先
 */
            // 2. 查找或创建标签
            Tag tag = tagMapper.selectByName(cleanName);
            if (tag == null) {
                tag = new Tag();
                tag.setName(cleanName);
                tag.setQuestionCount(0);
                tag.setCreatedAt(LocalDateTime.now());
                tagMapper.insert(tag);
                log.info("创建新标签: id={}, name={}", tag.getId(), cleanName);
            }

            // 3. 创建问题-标签关联
            QuestionTag questionTag = new QuestionTag();
            questionTag.setQuestionId(questionId);
            questionTag.setTagId(tag.getId());
            questionTag.setCreatedAt(LocalDateTime.now());
            questionTagMapper.insert(questionTag);

            // 4. 增加标签的问题计数
            tagMapper.increaseQuestionCount(tag.getId());

            // 5. 添加到结果
            TagVO tagVO = convertToVO(tag);
            result.add(tagVO);
        }

        return result;
    }

    @Override
    public List<TagVO> getTagsByQuestionId(Long questionId) {
        List<Long> tagIds = questionTagMapper.selectTagIdsByQuestionId(questionId);
        if (CollectionUtils.isEmpty(tagIds)) {
            return Collections.emptyList();
        }

        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.in("id", tagIds);
        List<Tag> tags = tagMapper.selectList(wrapper);

        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> getHotTags(int limit) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("question_count");
        wrapper.last("LIMIT " + limit);

        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagVO> searchTags(String keyword) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword);
        wrapper.orderByDesc("question_count");
        wrapper.last("LIMIT 20");

        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 删除问题的所有标签关联
     */
    @Transactional
    public void removeQuestionTags(Long questionId) {
        // 1. 获取问题的所有标签ID
        List<Long> tagIds = questionTagMapper.selectTagIdsByQuestionId(questionId);

        // 2. 删除关联记录
        QueryWrapper<QuestionTag> wrapper = new QueryWrapper<>();
        wrapper.eq("question_id", questionId);
        questionTagMapper.delete(wrapper);
        // 3. 减少标签的问题计数
        for (Long tagId : tagIds) {
            tagMapper.decreaseQuestionCount(tagId);
        }

        log.info("删除问题标签关联: questionId={}, 删除了{}个关联", questionId, tagIds.size());
    }

    private TagVO convertToVO(Tag tag) {
        TagVO vo = new TagVO();
        BeanUtil.copyProperties(tag, vo);
        return vo;
    }
}
