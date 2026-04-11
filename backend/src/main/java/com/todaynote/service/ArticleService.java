package com.todaynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.todaynote.dto.ArticleRequest;
import com.todaynote.dto.CommentRequest;
import com.todaynote.entity.Article;
import com.todaynote.vo.ArticleDetailVO;
import com.todaynote.vo.ArticleSummaryVO;
import com.todaynote.vo.CommentVO;

import java.util.List;

public interface ArticleService extends IService<Article> {
    /**
     * 查询当前作者的文章列表。
     */
    List<ArticleSummaryVO> listMine(Long userId, String keyword, String status, String startDate, String endDate, Long folderId);

    /**
     * 查询公开文章列表。
     */
    List<ArticleSummaryVO> listPublic(String keyword, String tag, String interest, String author, Long currentUserId);

    /**
     * 查询文章详情。
     */
    ArticleDetailVO getArticleById(Long id, Long currentUserId);

    /**
     * 创建文章。
     */
    Long createArticle(ArticleRequest request, Long userId);

    /**
     * 更新文章。
     */
    void updateArticle(Long id, ArticleRequest request, Long userId);

    /**
     * 发布文章。
     */
    void publishArticle(Long id, Long userId);

    /**
     * 取消发布文章。
     */
    void unpublishArticle(Long id, Long userId);

    /**
     * 删除文章。
     */
    void deleteArticle(Long id, Long userId);

    /**
     * 点赞文章。
     */
    void likeArticle(Long id, Long userId);

    /**
     * 取消点赞文章。
     */
    void unlikeArticle(Long id, Long userId);

    /**
     * 查询评论列表。
     */
    List<CommentVO> listComments(Long articleId, Long currentUserId);

    /**
     * 评论文章。
     */
    void commentArticle(Long articleId, CommentRequest request, Long userId);
}
