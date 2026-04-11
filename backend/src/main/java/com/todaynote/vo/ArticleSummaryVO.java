package com.todaynote.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ArticleSummaryVO {
    private Long id;
    private String title;
    private String summary;
    private Long authorId;
    private String authorName;
    private Long folderId;
    private String folderName;
    private String status;
    private String primaryInterestCode;
    private List<String> tags;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean editable;
}
