package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.Folder;
import com.wanwan.backend.service.FolderService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Resource
    private FolderService folderService;

    @GetMapping
    public ResponseResult<?> getAllFolders() {
        List<Folder> folders = folderService.list();
        return ResponseResult.success(folders);
    }

    @GetMapping("/{id}")
    public ResponseResult<?> getFolderById(@PathVariable Long id) {
        Folder folder = folderService.getById(id);
        if (folder != null) {
            return ResponseResult.success(folder);
        } else {
            return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST);
        }
    }

    @PostMapping
    public ResponseResult<?> createFolder(@RequestBody Folder folder) {
        boolean success = folderService.save(folder);
        if (success) {
            return ResponseResult.success("创建文件夹成功");
        } else {
            return ResponseResult.fail(ResultCode.FOLDER_EXIST);
        }
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        folder.setId(id);
        boolean success = folderService.updateById(folder);
        if (success) {
            return ResponseResult.success("更新文件夹成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "更新文件夹失败");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteFolder(@PathVariable Long id) {
        boolean success = folderService.removeById(id);
        if (success) {
            return ResponseResult.success("删除文件夹成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "删除文件夹失败");
        }
    }
}