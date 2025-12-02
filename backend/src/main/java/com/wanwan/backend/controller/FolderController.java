package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.Folder;
import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.FolderService;
import com.wanwan.backend.util.UserContext;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/folders")
public class FolderController {

    @Resource
    private FolderService folderService;

    /**
     * 获取用户的文件夹树结构
     */
    @GetMapping("/tree")
    public ResponseResult<?> getUserFolderTree() {
        try {
            Long userId = getCurrentUserId();
            List<?> folderTree = folderService.getUserFolderTree(userId);
            return ResponseResult.success(folderTree);
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "获取文件夹树失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户的所有文件夹（扁平结构）
     */
    @GetMapping
    public ResponseResult<?> getUserFolders() {
        try {
            Long userId = getCurrentUserId();
            List<Folder> folders = folderService.getUserFolders(userId);
            return ResponseResult.success(folders);
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "获取文件夹列表失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取文件夹详情
     */
    @GetMapping("/{id}")
    public ResponseResult<?> getFolderById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            Folder folder = folderService.getById(id);
            
            // 验证文件夹是否存在且属于当前用户
            if (folder == null || folder.getIsDelete() != 0) {
                return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST);
            }
            if (!folder.getUserId().equals(userId)) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权访问该文件夹");
            }
            
            return ResponseResult.success(folder);
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "获取文件夹详情失败：" + e.getMessage());
        }
    }

    /**
     * 创建新文件夹
     */
    @PostMapping
    public ResponseResult<?> createFolder(@RequestBody Folder folder) {
        try {
            Long userId = getCurrentUserId();
            folder.setUserId(userId);
            
            // 如果是子文件夹，验证父文件夹是否存在且属于当前用户
            if (folder.getParentId() != null) {
                Folder parentFolder = folderService.getById(folder.getParentId());
                log.info("parentFolder: {}", parentFolder);
                if (parentFolder == null || parentFolder.getIsDelete() != 0) {
                    return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST, "父文件夹不存在");
                }
                if (!parentFolder.getUserId().equals(userId)) {
                    return ResponseResult.fail(ResultCode.FORBIDDEN, "无权访问该父文件夹");
                }
                
                // 设置ancestors字段
                if (parentFolder.getAncestors() != null) {
                    folder.setAncestors(parentFolder.getAncestors() + "," + parentFolder.getId());
                } else {
                    folder.setAncestors(parentFolder.getId().toString());
                }
            }
            
            boolean success = folderService.save(folder);
            if (success) {
                return ResponseResult.success("创建文件夹成功");
            } else {
                return ResponseResult.fail(ResultCode.FOLDER_EXIST);
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "创建文件夹失败：" + e.getMessage());
        }
    }

    /**
     * 更新文件夹信息
     */
    @PutMapping("/{id}")
    public ResponseResult<?> updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        try {
            Long userId = getCurrentUserId();
            folder.setId(id);
            
            // 验证文件夹是否存在且属于当前用户
            Folder existingFolder = folderService.getById(id);
            if (existingFolder == null || existingFolder.getIsDelete() != 0) {
                return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST);
            }
            if (!existingFolder.getUserId().equals(userId)) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权修改该文件夹");
            }
            
            // 不允许修改userId
            folder.setUserId(userId);
            
            // 如果修改了父文件夹，需要更新ancestors字段
            if (folder.getParentId() != null && !folder.getParentId().equals(existingFolder.getParentId())) {
                Folder parentFolder = folderService.getById(folder.getParentId());
                if (parentFolder == null || parentFolder.getIsDelete() != 0) {
                    return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST, "父文件夹不存在");
                }
                if (!parentFolder.getUserId().equals(userId)) {
                    return ResponseResult.fail(ResultCode.FORBIDDEN, "无权访问该父文件夹");
                }
                
                // 设置新的ancestors
                if (parentFolder.getAncestors() != null) {
                    folder.setAncestors(parentFolder.getAncestors() + "," + parentFolder.getId());
                } else {
                    folder.setAncestors(parentFolder.getId().toString());
                }
                
                // TODO: 更新所有子文件夹的ancestors字段
            }
            
            boolean success = folderService.updateById(folder);
            if (success) {
                return ResponseResult.success("更新文件夹成功");
            } else {
                return ResponseResult.fail(ResultCode.ERROR, "更新文件夹失败");
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "更新文件夹失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件夹（软删除）
     */
    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteFolder(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            
            // 验证文件夹是否存在且属于当前用户
            Folder folder = folderService.getById(id);
            if (folder == null || folder.getIsDelete() != 0) {
                return ResponseResult.fail(ResultCode.FOLDER_NOT_EXIST);
            }
            if (!folder.getUserId().equals(userId)) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权删除该文件夹");
            }
            
            // 检查是否有子文件夹，如果有则不允许删除
            List<Folder> children = folderService.getFolderWithChildren(id);
            if (children.size() > 1) { // 大于1表示有子文件夹
                return ResponseResult.fail(ResultCode.ERROR, "该文件夹下有子文件夹，请先删除子文件夹");
            }
            
            // 软删除文件夹
            folder.setIsDelete(1);
            boolean success = folderService.updateById(folder);
            
            if (success) {
                return ResponseResult.success("删除文件夹成功");
            } else {
                return ResponseResult.fail(ResultCode.ERROR, "删除文件夹失败");
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "删除文件夹失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取当前登录用户的ID
     */
    private Long getCurrentUserId() {
        User currentUser = UserContext.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("用户未登录");
        }
        return currentUser.getId();
    }
}