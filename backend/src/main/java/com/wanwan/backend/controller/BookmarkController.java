package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.Bookmark;
import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.BookmarkService;
import com.wanwan.backend.util.UserContext;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Resource
    private BookmarkService bookmarkService;

    @GetMapping
    public ResponseResult<?> getAllBookmarks() {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseResult.fail(ResultCode.UNAUTHORIZED, "用户未登录");
            }
            List<Bookmark> bookmarks = bookmarkService.getUserBookmarks(currentUser.getId());
            return ResponseResult.success(bookmarks);
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "获取书签列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseResult<?> getBookmarkById(@PathVariable Long id) {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseResult.fail(ResultCode.UNAUTHORIZED, "用户未登录");
            }
            
            Bookmark bookmark = bookmarkService.getById(id);
            if (bookmark == null) {
                return ResponseResult.fail(ResultCode.NOT_FOUND);
            }
            
            // 验证书签是否属于当前用户
            if (!bookmark.getUserId().equals(currentUser.getId())) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权访问该书签");
            }
            
            return ResponseResult.success(bookmark);
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "获取书签详情失败：" + e.getMessage());
        }
    }

    @PostMapping
    public ResponseResult<?> createBookmark(@RequestBody Bookmark bookmark) {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseResult.fail(ResultCode.UNAUTHORIZED, "用户未登录");
            }
            
            // 设置书签所属用户
            bookmark.setUserId(currentUser.getId());
            
            boolean success = bookmarkService.save(bookmark);
            if (success) {
                return ResponseResult.success("创建书签成功");
            } else {
                return ResponseResult.fail(ResultCode.ERROR, "创建书签失败");
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "创建书签失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseResult.fail(ResultCode.UNAUTHORIZED, "用户未登录");
            }
            
            bookmark.setId(id);
            
            // 验证书签是否存在且属于当前用户
            Bookmark existingBookmark = bookmarkService.getById(id);
            if (existingBookmark == null) {
                return ResponseResult.fail(ResultCode.NOT_FOUND);
            }
            if (!existingBookmark.getUserId().equals(currentUser.getId())) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权修改该书签");
            }
            
            // 确保不会修改用户ID
            bookmark.setUserId(currentUser.getId());
            
            boolean success = bookmarkService.updateById(bookmark);
            if (success) {
                return ResponseResult.success("更新书签成功");
            } else {
                return ResponseResult.fail(ResultCode.ERROR, "更新书签失败");
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "更新书签失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteBookmark(@PathVariable Long id) {
        try {
            User currentUser = UserContext.getCurrentUser();
            if (currentUser == null) {
                return ResponseResult.fail(ResultCode.UNAUTHORIZED, "用户未登录");
            }
            
            // 验证书签是否存在且属于当前用户
            Bookmark bookmark = bookmarkService.getById(id);
            if (bookmark == null) {
                return ResponseResult.fail(ResultCode.NOT_FOUND);
            }
            if (!bookmark.getUserId().equals(currentUser.getId())) {
                return ResponseResult.fail(ResultCode.FORBIDDEN, "无权删除该书签");
            }
            
            boolean success = bookmarkService.removeById(id);
            if (success) {
                return ResponseResult.success("删除书签成功");
            } else {
                return ResponseResult.fail(ResultCode.ERROR, "删除书签失败");
            }
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "删除书签失败：" + e.getMessage());
        }
    }
}