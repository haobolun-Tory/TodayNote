package com.todaynote.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentVO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private Boolean mine;
}
