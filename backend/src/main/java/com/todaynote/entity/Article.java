package com.todaynote.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("author_id")
    private Long userId;

    private String title;

    private String summary;

    @TableField("content_md")
    private String content;

    @TableField("folder_id")
    private Long folderId;

    private String tags;

    private String status;

    @TableField("primary_interest_code")
    private String primaryInterestCode;

    private Long viewCount;

    private Long likeCount;

    private Long commentCount;

    @TableField("published_at")
    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private LocalDateTime deletedAt;
}
