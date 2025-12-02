package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.Folder;
import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.FolderService;
import com.wanwan.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Resource
    private UserService userService;
    
    @Resource
    private FolderService folderService;

    @GetMapping
    public ResponseResult<?> getAllUsers() {
        List<User> users = userService.list();
        return ResponseResult.success(users);
    }

    @GetMapping("/{id}")
    public ResponseResult<?> getUserById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            return ResponseResult.success(user);
        } else {
            return ResponseResult.fail(ResultCode.USER_NOT_EXIST);
        }
    }

    @PostMapping
    public ResponseResult<?> createUser(@RequestBody User user) {
        boolean success = userService.save(user);
        if (success) {
            // 创建用户的根文件夹
            Folder rootFolder = new Folder();
            rootFolder.setName("我的收藏");
            rootFolder.setParentId(null);
            rootFolder.setUserId(user.getId());
            rootFolder.setAncestors(null);
            rootFolder.setCreatedTime(LocalDateTime.now());
            rootFolder.setUpdatedTime(LocalDateTime.now());
            rootFolder.setIsDelete(0);
            folderService.save(rootFolder);
            
            return ResponseResult.success("创建用户成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "创建用户失败");
        }
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateById(user);
        if (success) {
            return ResponseResult.success("更新用户成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "更新用户失败");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteUser(@PathVariable Long id) {
        boolean success = userService.removeById(id);
        if (success) {
            return ResponseResult.success("删除用户成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "删除用户失败");
        }
    }
}