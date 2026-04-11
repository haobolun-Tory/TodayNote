package com.todaynote.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.todaynote.dto.FolderRequest;
import com.todaynote.entity.Folder;

import java.util.List;

public interface FolderService extends IService<Folder> {
    /**
     * 查询当前用户的文件夹列表。
     */
    List<Folder> listMine(Long userId);

    /**
     * 创建文件夹。
     */
    Folder createFolder(FolderRequest request, Long userId);

    /**
     * 更新文件夹信息。
     */
    Folder updateFolder(Long id, FolderRequest request, Long userId);

    /**
     * 删除文件夹。
     */
    void deleteFolder(Long id, Long userId);
}
