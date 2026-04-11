package com.todaynote.controller;

import com.todaynote.common.Result;
import com.todaynote.dto.FolderRequest;
import com.todaynote.entity.Folder;
import com.todaynote.service.FolderService;
import com.todaynote.support.AuthHelper;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
/**
 * 作者文件夹管理接口。
 */
public class FolderController {

    private final FolderService folderService;
    private final AuthHelper authHelper;

    /**
     * 获取当前用户的文件夹列表。
     *
     * @param request HTTP 请求
     * @return 文件夹列表
     */
    @GetMapping("/mine")
    public Result<List<Folder>> listMine(HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        return Result.success(folderService.listMine(userId));
    }

    /**
     * 创建文件夹。
     *
     * @param requestBody 文件夹请求体
     * @param request HTTP 请求
     * @return 创建后的文件夹
     */
    @PostMapping
    public Result<Folder> create(@RequestBody @Valid FolderRequest requestBody, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        return Result.success(folderService.createFolder(requestBody, userId));
    }

    /**
     * 更新文件夹信息。
     *
     * @param id 文件夹 ID
     * @param requestBody 文件夹请求体
     * @param request HTTP 请求
     * @return 更新后的文件夹
     */
    @PutMapping("/{id}")
    public Result<Folder> update(@PathVariable Long id, @RequestBody @Valid FolderRequest requestBody, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        return Result.success(folderService.updateFolder(id, requestBody, userId));
    }

    /**
     * 删除文件夹。
     *
     * @param id 文件夹 ID
     * @param request HTTP 请求
     * @return 空成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = authHelper.requireCurrentUserId(request);
        folderService.deleteFolder(id, userId);
        return Result.success();
    }
}
