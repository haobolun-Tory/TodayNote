package com.todaynote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.todaynote.common.BizException;
import com.todaynote.dto.FolderRequest;
import com.todaynote.entity.Article;
import com.todaynote.entity.Folder;
import com.todaynote.mapper.FolderMapper;
import com.todaynote.service.ArticleService;
import com.todaynote.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
/**
 * 文件夹领域服务实现。
 */
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    private final ArticleService articleService;

    /**
     * 查询当前用户的文件夹列表。
     */
    @Override
    public List<Folder> listMine(Long userId) {
        return list(new LambdaQueryWrapper<Folder>()
                .eq(Folder::getUserId, userId)
                .orderByAsc(Folder::getSortOrder)
                .orderByDesc(Folder::getCreatedAt));
    }

    /**
     * 创建文件夹。
     */
    @Override
    public Folder createFolder(FolderRequest request, Long userId) {
        // 按照请求参数组装文件夹实体。
        Folder folder = new Folder();
        folder.setUserId(userId);
        folder.setName(request.getName().trim());
        folder.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
        folder.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        save(folder);
        return folder;
    }

    /**
     * 更新文件夹。
     */
    @Override
    public Folder updateFolder(Long id, FolderRequest request, Long userId) {
        Folder folder = getOwnedFolder(id, userId);
        folder.setName(request.getName().trim());
        folder.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
        folder.setSortOrder(request.getSortOrder() == null ? 0 : request.getSortOrder());
        updateById(folder);
        return folder;
    }

    /**
     * 删除文件夹，并把原有归档文章恢复为未归档状态。
     */
    @Override
    public void deleteFolder(Long id, Long userId) {
        Folder folder = getOwnedFolder(id, userId);
        // 删除文件夹前先解除文章与文件夹的关联关系。
        articleService.update(new LambdaUpdateWrapper<Article>()
                .eq(Article::getUserId, userId)
                .eq(Article::getFolderId, folder.getId())
                .set(Article::getFolderId, null));
        removeById(folder.getId());
    }

    /**
     * 获取指定用户拥有的文件夹。
     */
    private Folder getOwnedFolder(Long id, Long userId) {
        Folder folder = getOne(new LambdaQueryWrapper<Folder>()
                .eq(Folder::getId, id)
                .eq(Folder::getUserId, userId));
        if (folder == null) {
            throw new BizException(40400, "文件夹不存在");
        }
        return folder;
    }
}
