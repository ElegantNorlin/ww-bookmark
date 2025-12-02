package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.Folder;
import com.wanwan.backend.mapper.FolderMapper;
import com.wanwan.backend.service.FolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {
    
    @Override
    public List<Map<String, Object>> getUserFolderTree(String userId) {
        // 获取用户的所有文件夹
        List<Folder> folders = getUserFolders(userId);
        // 构建并返回文件夹树结构
        return buildFolderTree(folders, null);
    }
    
    @Override
    public List<Map<String, Object>> buildFolderTree(List<Folder> folders, String parentId) {
        List<Map<String, Object>> folderTree = new ArrayList<>();
        
        // 找出所有顶级文件夹（parentId为null的文件夹）
        folders.stream()
                .filter(folder -> Objects.equals(folder.getParentId(), parentId) || 
                                 (parentId == null && folder.getParentId() == null))
                .forEach(folder -> {
                    Map<String, Object> folderNode = convertToTreeNode(folder);
                    // 递归查找子文件夹
                    folderNode.put("children", findChildren(folders, folder.getId()));
                    folderTree.add(folderNode);
                });
        
        return folderTree;
    }
    
    @Override
    public List<Folder> getUserFolders(String userId) {
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("is_delete", 0)
                   .orderByAsc("created_time");
        return list(queryWrapper);
    }
    
    @Override
    public List<Folder> getFolderWithChildren(String folderId) {
        List<Folder> result = new ArrayList<>();
        // 首先获取指定的文件夹
        Folder folder = getById(folderId);
        if (folder != null && folder.getIsDelete() == 0) {
            result.add(folder);
            // 递归获取所有子文件夹
            addChildren(result, folderId);
        }
        return result;
    }
    
    /**
     * 将Folder对象转换为树节点Map
     */
    private Map<String, Object> convertToTreeNode(Folder folder) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", folder.getId());
        node.put("name", folder.getName());
        node.put("parentId", folder.getParentId());
        node.put("userId", folder.getUserId());
        node.put("ancestors", folder.getAncestors());
        node.put("createdTime", folder.getCreatedTime());
        node.put("updatedTime", folder.getUpdatedTime());
        return node;
    }
    
    /**
     * 查找指定父文件夹的所有子文件夹
     */
    private List<Map<String, Object>> findChildren(List<Folder> folders, String parentId) {
        List<Map<String, Object>> children = new ArrayList<>();
        
        folders.stream()
                .filter(folder -> Objects.equals(folder.getParentId(), parentId))
                .forEach(folder -> {
                    Map<String, Object> childNode = convertToTreeNode(folder);
                    // 递归查找子文件夹的子文件夹
                    childNode.put("children", findChildren(folders, folder.getId()));
                    children.add(childNode);
                });
        
        return children;
    }
    
    /**
     * 递归添加所有子文件夹
     */
    private void addChildren(List<Folder> result, String parentId) {
        QueryWrapper<Folder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId)
                   .eq("is_delete", 0);
        List<Folder> children = list(queryWrapper);
        
        for (Folder child : children) {
            result.add(child);
            // 递归获取子文件夹的子文件夹
            addChildren(result, child.getId());
        }
    }
}