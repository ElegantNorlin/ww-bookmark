package com.wanwan.backend.service;

import com.wanwan.backend.entity.Folder;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;

public interface FolderService extends IService<Folder> {
    
    /**
     * 获取用户的文件夹树结构
     * @param userId 用户ID
     * @return 文件夹树结构
     */
    List<Map<String, Object>> getUserFolderTree(Long userId);
    
    /**
     * 构建文件夹树结构
     * @param folders 文件夹列表
     * @param parentId 父文件夹ID
     * @return 文件夹树结构
     */
    List<Map<String, Object>> buildFolderTree(List<Folder> folders, Long parentId);
    
    /**
     * 获取用户的所有文件夹
     * @param userId 用户ID
     * @return 文件夹列表
     */
    List<Folder> getUserFolders(Long userId);
    
    /**
     * 获取文件夹及其所有子文件夹
     * @param folderId 文件夹ID
     * @return 文件夹列表
     */
    List<Folder> getFolderWithChildren(Long folderId);
}