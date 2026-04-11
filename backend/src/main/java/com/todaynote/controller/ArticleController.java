package com.todaynote.controller;

import com.todaynote.common.Result;
import com.todaynote.dto.ArticleRequest;
import com.todaynote.dto.CommentRequest;
import com.todaynote.service.ArticleService;
import com.todaynote.support.AuthHelper;
import com.todaynote.vo.ArticleDetailVO;
import com.todaynote.vo.ArticleSummaryVO;
import com.todaynote.vo.CommentVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
/**
 * 文章相关接口控制器，覆盖作者域与公开域的核心能力。
 */
public class ArticleController {

    private final ArticleService articleService;
    private final AuthHelper authHelper;

    /**
     * 获取当前作者的文章列表。
     */
    @GetMapping("/mine")
    public Result<List<ArticleSummaryVO>> listMine(@RequestParam(required = false) String keyword,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String startDate,
                                                   @RequestParam(required = false) String endDate,
                                                   @RequestParam(required = false) Long folderId,
                                                   HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        return Result.success(articleService.listMine(userId, keyword, status, startDate, endDate, folderId));
    }

    /**
     * 获取公开文章列表，支持关键字、标签、兴趣和作者筛选。
     */
    @GetMapping
    public Result<List<ArticleSummaryVO>> listPublic(@RequestParam(required = false, name = "q") String keyword,
                                                     @RequestParam(required = false) String tag,
                                                     @RequestParam(required = false) String interest,
                                                     @RequestParam(required = false) String author,
                                                     HttpServletRequest request) {
        Long currentUserId = authHelper.getCurrentUserId(request);
        return Result.success(articleService.listPublic(keyword, tag, interest, author, currentUserId));
    }

    /**
     * 获取单篇文章详情。
     */
    @GetMapping("/{id}")
    public Result<ArticleDetailVO> getArticle(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = authHelper.getCurrentUserId(request);
        return Result.success(articleService.getArticleById(id, currentUserId));
    }

    /**
     * 创建新文章。
     */
    @PostMapping
    public Result<Map<String, Long>> createArticle(@RequestBody @Valid ArticleRequest articleRequest, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        Long articleId = articleService.createArticle(articleRequest, userId);
        return Result.success(Map.of("id", articleId));
    }

    /**
     * 更新已有文章。
     */
    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @RequestBody @Valid ArticleRequest articleRequest, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.updateArticle(id, articleRequest, userId);
        return Result.success();
    }

    /**
     * 发布文章到公开域。
     */
    @PostMapping("/{id}/publish")
    public Result<Void> publishArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.publishArticle(id, userId);
        return Result.success();
    }

    /**
     * 取消发布文章并退回草稿态。
     */
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublishArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.unpublishArticle(id, userId);
        return Result.success();
    }

    /**
     * 删除当前作者的文章。
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.deleteArticle(id, userId);
        return Result.success();
    }

    /**
     * 为公开文章点赞。
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.likeArticle(id, userId);
        return Result.success();
    }

    /**
     * 取消点赞。
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeArticle(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.unlikeArticle(id, userId);
        return Result.success();
    }

    /**
     * 获取文章评论列表。
     */
    @GetMapping("/{id}/comments")
    public Result<List<CommentVO>> listComments(@PathVariable Long id, HttpServletRequest request) {
        Long currentUserId = authHelper.getCurrentUserId(request);
        return Result.success(articleService.listComments(id, currentUserId));
    }

    /**
     * 提交文章评论。
     */
    @PostMapping("/{id}/comments")
    public Result<Void> commentArticle(@PathVariable Long id, @RequestBody @Valid CommentRequest requestBody, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        articleService.commentArticle(id, requestBody, userId);
        return Result.success();
    }
}

