package com.todaynote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todaynote.common.BizException;
import com.todaynote.dto.ArticleRequest;
import com.todaynote.dto.CommentRequest;
import com.todaynote.entity.Article;
import com.todaynote.entity.ArticleComment;
import com.todaynote.entity.ArticleLike;
import com.todaynote.entity.Folder;
import com.todaynote.entity.User;
import com.todaynote.mapper.ArticleCommentMapper;
import com.todaynote.mapper.ArticleLikeMapper;
import com.todaynote.mapper.ArticleMapper;
import com.todaynote.mapper.FolderMapper;
import com.todaynote.service.ArticleService;
import com.todaynote.service.UserService;
import com.todaynote.vo.ArticleDetailVO;
import com.todaynote.vo.ArticleSummaryVO;
import com.todaynote.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
/**
 * 文章领域服务实现，负责文章、点赞与评论等核心业务。
 */
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final String STATUS_DRAFT = "DRAFT";
    private static final String STATUS_PUBLISHED = "PUBLISHED";

    private final UserService userService;
    private final FolderMapper folderMapper;
    private final ArticleLikeMapper articleLikeMapper;
    private final ArticleCommentMapper articleCommentMapper;

    /**
     * 获取当前作者自己的文章列表，并在内存中完成筛选。
     */
    @Override
    public List<ArticleSummaryVO> listMine(Long userId, String keyword, String status, String startDate, String endDate, Long folderId) {
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getUserId, userId)
                .orderByDesc(Article::getUpdatedAt)
                .orderByDesc(Article::getCreatedAt));
        return buildSummaryList(articles, userId).stream()
                .filter(article -> matchMine(article, keyword, status, startDate, endDate, folderId))
                .toList();
    }

    /**
     * 获取公开文章列表，并按公开域条件过滤。
     */
    @Override
    public List<ArticleSummaryVO> listPublic(String keyword, String tag, String interest, String author, Long currentUserId) {
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, STATUS_PUBLISHED)
                .orderByDesc(Article::getPublishedAt)
                .orderByDesc(Article::getUpdatedAt));
        return buildSummaryList(articles, currentUserId).stream()
                .filter(article -> matchPublic(article, keyword, tag, interest, author))
                .toList();
    }

    /**
     * 获取文章详情，并在访问公开文章时累计浏览量。
     */
    @Override
    public ArticleDetailVO getArticleById(Long id, Long currentUserId) {
        Article article = getById(id);
        if (article == null) {
            throw new BizException(40400, "文章不存在");
        }
        // 允许作者本人查看草稿，但访客和其他用户只能查看已发布文章。
        boolean editable = currentUserId != null && Objects.equals(article.getUserId(), currentUserId);
        if (!STATUS_PUBLISHED.equals(article.getStatus()) && !editable) {
            throw new BizException(40300, "该文章尚未发布");
        }

        // 详情页访问成功后累计浏览量。
        article.setViewCount(defaultLong(article.getViewCount()) + 1);
        updateById(article);
        return toDetailVO(article, currentUserId);
    }

    /**
     * 创建文章草稿。
     */
    @Override
    public Long createArticle(ArticleRequest request, Long userId) {
        validateFolderOwner(request.getFolderId(), userId);
        Article article = new Article();
        // 统一填充文章通用字段，避免创建和更新逻辑重复。
        fillArticle(article, request);
        article.setUserId(userId);
        article.setStatus(STATUS_DRAFT);
        article.setPrimaryInterestCode(resolveInterest(request.getTags()));
        article.setViewCount(0L);
        article.setLikeCount(0L);
        article.setCommentCount(0L);
        save(article);
        return article.getId();
    }

    /**
     * 更新当前作者自己的文章。
     */
    @Override
    public void updateArticle(Long id, ArticleRequest request, Long userId) {
        validateFolderOwner(request.getFolderId(), userId);
        Article article = getOwnedArticle(id, userId);
        fillArticle(article, request);
        article.setPrimaryInterestCode(resolveInterest(request.getTags()));
        updateById(article);
    }

    /**
     * 发布文章到公开域。
     */
    @Override
    public void publishArticle(Long id, Long userId) {
        Article article = getOwnedArticle(id, userId);
        // 发布前确保最基本的内容完整性。
        if (!StringUtils.hasText(article.getTitle()) || !StringUtils.hasText(article.getContent())) {
            throw new BizException(40001, "标题和正文不能为空");
        }
        article.setStatus(STATUS_PUBLISHED);
        article.setPrimaryInterestCode(resolveInterest(article.getTags()));
        article.setPublishedAt(LocalDateTime.now());
        updateById(article);
    }

    /**
     * 取消发布文章。
     */
    @Override
    public void unpublishArticle(Long id, Long userId) {
        Article article = getOwnedArticle(id, userId);
        article.setStatus(STATUS_DRAFT);
        article.setPublishedAt(null);
        updateById(article);
    }

    /**
     * 删除文章。
     */
    @Override
    public void deleteArticle(Long id, Long userId) {
        Article article = getOwnedArticle(id, userId);
        removeById(article.getId());
    }

    /**
     * 点赞公开文章，重复点赞直接忽略。
     */
    @Override
    public void likeArticle(Long id, Long userId) {
        Article article = getPublishedArticle(id);
        ArticleLike existing = articleLikeMapper.selectOne(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, id)
                .eq(ArticleLike::getUserId, userId));
        if (existing != null) {
            return;
        }
        ArticleLike articleLike = new ArticleLike();
        articleLike.setArticleId(id);
        articleLike.setUserId(userId);
        articleLikeMapper.insert(articleLike);
        article.setLikeCount(defaultLong(article.getLikeCount()) + 1);
        updateById(article);
    }

    /**
     * 取消点赞并重新计算文章点赞数。
     */
    @Override
    public void unlikeArticle(Long id, Long userId) {
        Article article = getPublishedArticle(id);
        articleLikeMapper.delete(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, id)
                .eq(ArticleLike::getUserId, userId));
        long count = articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, id));
        article.setLikeCount(count);
        updateById(article);
    }

    /**
     * 查询文章评论列表。
     */
    @Override
    public List<CommentVO> listComments(Long articleId, Long currentUserId) {
        Article article = getById(articleId);
        if (article == null) {
            throw new BizException(40400, "文章不存在");
        }
        // 作者可以查看自己草稿文章下的评论，其他人只能查看公开文章评论。
        boolean editable = currentUserId != null && Objects.equals(article.getUserId(), currentUserId);
        if (!STATUS_PUBLISHED.equals(article.getStatus()) && !editable) {
            throw new BizException(40300, "该文章尚未发布");
        }

        List<ArticleComment> comments = articleCommentMapper.selectList(new LambdaQueryWrapper<ArticleComment>()
                .eq(ArticleComment::getArticleId, articleId)
                .orderByDesc(ArticleComment::getCreatedAt));
        Map<Long, User> userMap = getUserMap(comments.stream().map(ArticleComment::getUserId).collect(Collectors.toSet()));
        return comments.stream().map(comment -> {
            User user = userMap.get(comment.getUserId());
            return CommentVO.builder()
                    .id(comment.getId())
                    .userId(comment.getUserId())
                    .username(user == null ? "unknown" : user.getUsername())
                    .nickname(user == null ? "未知用户" : firstNonBlank(user.getNickname(), user.getUsername()))
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .mine(currentUserId != null && Objects.equals(comment.getUserId(), currentUserId))
                    .build();
        }).toList();
    }

    /**
     * 提交文章评论。
     */
    @Override
    public void commentArticle(Long articleId, CommentRequest request, Long userId) {
        Article article = getPublishedArticle(articleId);
        ArticleComment comment = new ArticleComment();
        comment.setArticleId(articleId);
        comment.setUserId(userId);
        comment.setContent(request.getContent().trim());
        articleCommentMapper.insert(comment);
        article.setCommentCount(defaultLong(article.getCommentCount()) + 1);
        updateById(article);
    }

    /**
     * 批量构建文章摘要视图列表。
     */
    private List<ArticleSummaryVO> buildSummaryList(List<Article> articles, Long currentUserId) {
        Set<Long> userIds = articles.stream().map(Article::getUserId).collect(Collectors.toSet());
        Set<Long> folderIds = articles.stream().map(Article::getFolderId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = getUserMap(userIds);
        Map<Long, Folder> folderMap = getFolderMap(folderIds);
        return articles.stream().map(article -> toSummaryVO(article, currentUserId, userMap, folderMap)).toList();
    }

    /**
     * 把文章实体转换为列表视图对象。
     */
    private ArticleSummaryVO toSummaryVO(Article article, Long currentUserId, Map<Long, User> userMap, Map<Long, Folder> folderMap) {
        User user = userMap.get(article.getUserId());
        Folder folder = article.getFolderId() == null ? null : folderMap.get(article.getFolderId());
        return ArticleSummaryVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .summary(firstNonBlank(article.getSummary(), buildSummaryFromContent(article.getContent())))
                .authorId(article.getUserId())
                .authorName(user == null ? "未知作者" : firstNonBlank(user.getNickname(), user.getUsername()))
                .folderId(article.getFolderId())
                .folderName(folder == null ? null : folder.getName())
                .status(article.getStatus())
                .primaryInterestCode(article.getPrimaryInterestCode())
                .tags(parseTags(article.getTags()))
                .viewCount(defaultLong(article.getViewCount()))
                .likeCount(defaultLong(article.getLikeCount()))
                .commentCount(defaultLong(article.getCommentCount()))
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .editable(currentUserId != null && Objects.equals(article.getUserId(), currentUserId))
                .build();
    }

    /**
     * 构建文章详情视图对象。
     */
    private ArticleDetailVO toDetailVO(Article article, Long currentUserId) {
        ArticleSummaryVO summary = buildSummaryList(Collections.singletonList(article), currentUserId).getFirst();
        boolean liked = currentUserId != null && articleLikeMapper.selectCount(new LambdaQueryWrapper<ArticleLike>()
                .eq(ArticleLike::getArticleId, article.getId())
                .eq(ArticleLike::getUserId, currentUserId)) > 0;
        return ArticleDetailVO.builder()
                .id(summary.getId())
                .title(summary.getTitle())
                .summary(summary.getSummary())
                .content(article.getContent())
                .authorId(summary.getAuthorId())
                .authorName(summary.getAuthorName())
                .folderId(summary.getFolderId())
                .folderName(summary.getFolderName())
                .status(summary.getStatus())
                .primaryInterestCode(summary.getPrimaryInterestCode())
                .tags(summary.getTags())
                .viewCount(summary.getViewCount())
                .likeCount(summary.getLikeCount())
                .commentCount(summary.getCommentCount())
                .publishedAt(summary.getPublishedAt())
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .editable(summary.getEditable())
                .likedByCurrentUser(liked)
                .build();
    }

    /**
     * 批量加载用户信息并转为 Map。
     */
    private Map<Long, User> getUserMap(Set<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userService.listByIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }

    /**
     * 批量加载文件夹信息并转为 Map。
     */
    private Map<Long, Folder> getFolderMap(Set<Long> folderIds) {
        if (folderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return folderMapper.selectBatchIds(folderIds).stream().collect(Collectors.toMap(Folder::getId, Function.identity()));
    }

    /**
     * 作者域文章筛选规则。
     */
    private boolean matchMine(ArticleSummaryVO article, String keyword, String status, String startDate, String endDate, Long folderId) {
        if (StringUtils.hasText(keyword)) {
            String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);
            boolean matched = article.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                    || (article.getSummary() != null && article.getSummary().toLowerCase(Locale.ROOT).contains(lowerKeyword));
            if (!matched) {
                return false;
            }
        }
        if (StringUtils.hasText(status) && !status.equalsIgnoreCase(article.getStatus())) {
            return false;
        }
        if (folderId != null && !Objects.equals(folderId, article.getFolderId())) {
            return false;
        }
        LocalDate current = (article.getUpdatedAt() == null ? article.getCreatedAt() : article.getUpdatedAt()).toLocalDate();
        if (StringUtils.hasText(startDate) && current.isBefore(LocalDate.parse(startDate))) {
            return false;
        }
        if (StringUtils.hasText(endDate) && current.isAfter(LocalDate.parse(endDate))) {
            return false;
        }
        return true;
    }

    /**
     * 公开域文章筛选规则。
     */
    private boolean matchPublic(ArticleSummaryVO article, String keyword, String tag, String interest, String author) {
        if (StringUtils.hasText(keyword)) {
            String lowerKeyword = keyword.trim().toLowerCase(Locale.ROOT);
            boolean matched = article.getTitle().toLowerCase(Locale.ROOT).contains(lowerKeyword)
                    || (article.getSummary() != null && article.getSummary().toLowerCase(Locale.ROOT).contains(lowerKeyword));
            if (!matched) {
                return false;
            }
        }
        if (StringUtils.hasText(tag) && article.getTags().stream().noneMatch(item -> item.equalsIgnoreCase(tag.trim()))) {
            return false;
        }
        if (StringUtils.hasText(interest) && !interest.equalsIgnoreCase(article.getPrimaryInterestCode())) {
            return false;
        }
        if (StringUtils.hasText(author)) {
            String lowerAuthor = author.trim().toLowerCase(Locale.ROOT);
            if (article.getAuthorName() == null || !article.getAuthorName().toLowerCase(Locale.ROOT).contains(lowerAuthor)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前作者拥有的文章。
     */
    private Article getOwnedArticle(Long id, Long userId) {
        Article article = getOne(new LambdaQueryWrapper<Article>()
                .eq(Article::getId, id)
                .eq(Article::getUserId, userId));
        if (article == null) {
            throw new BizException(40400, "文章不存在或无权访问");
        }
        return article;
    }

    /**
     * 获取已发布文章。
     */
    private Article getPublishedArticle(Long id) {
        Article article = getById(id);
        if (article == null) {
            throw new BizException(40400, "文章不存在");
        }
        if (!STATUS_PUBLISHED.equals(article.getStatus())) {
            throw new BizException(40300, "文章尚未发布");
        }
        return article;
    }

    /**
     * 校验文件夹是否属于当前用户。
     */
    private void validateFolderOwner(Long folderId, Long userId) {
        if (folderId == null) {
            return;
        }
        Folder folder = folderMapper.selectById(folderId);
        if (folder == null || !Objects.equals(folder.getUserId(), userId)) {
            throw new BizException(40300, "只能归档到自己的文件夹");
        }
    }

    /**
     * 统一填充文章的可编辑字段。
     */
    private void fillArticle(Article article, ArticleRequest request) {
        article.setTitle(request.getTitle().trim());
        article.setSummary(StringUtils.hasText(request.getSummary()) ? request.getSummary().trim() : buildSummaryFromContent(request.getContent()));
        article.setContent(request.getContent().trim());
        article.setTags(normalizeTags(request.getTags()));
        article.setFolderId(request.getFolderId());
    }

    /**
     * 根据标签推断文章所属兴趣。
     */
    private String resolveInterest(String tags) {
        List<String> tagList = parseTags(tags).stream().map(String::toLowerCase).toList();
        if (tagList.stream().anyMatch(tag -> List.of("java", "spring", "springboot", "mysql", "mybatis").contains(tag))) {
            return "java-backend";
        }
        if (tagList.stream().anyMatch(tag -> List.of("vue", "javascript", "typescript", "css", "html", "vite").contains(tag))) {
            return "frontend";
        }
        if (tagList.stream().anyMatch(tag -> List.of("读书", "阅读", "书评", "写作").contains(tag))) {
            return "reading";
        }
        if (tagList.stream().anyMatch(tag -> List.of("生活", "随笔", "成长", "复盘").contains(tag))) {
            return "life";
        }
        return tagList.isEmpty() ? "general" : "other";
    }

    /**
     * 规范化标签存储格式。
     */
    private String normalizeTags(String tags) {
        return String.join(",", parseTags(tags));
    }

    /**
     * 把逗号分隔的标签字符串转换为列表。
     */
    private List<String> parseTags(String tags) {
        if (!StringUtils.hasText(tags)) {
            return Collections.emptyList();
        }
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    /**
     * 根据正文内容生成默认摘要。
     */
    private String buildSummaryFromContent(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String plainText = content.replaceAll("[#>*`\\-]", " ").replaceAll("\\s+", " ").trim();
        return plainText.length() <= 120 ? plainText : plainText.substring(0, 120) + "...";
    }

    /**
     * 返回第一个非空字符串。
     */
    private String firstNonBlank(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }

    /**
     * 把空数值转换为 0，便于前端展示统计字段。
     */
    private Long defaultLong(Long value) {
        return value == null ? 0L : value;
    }
}
