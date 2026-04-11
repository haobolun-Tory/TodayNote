package com.todaynote.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.todaynote.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}
