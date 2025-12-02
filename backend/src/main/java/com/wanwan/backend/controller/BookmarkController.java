package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.Bookmark;
import com.wanwan.backend.service.BookmarkService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {

    @Resource
    private BookmarkService bookmarkService;

    @GetMapping
    public ResponseResult<?> getAllBookmarks() {
        List<Bookmark> bookmarks = bookmarkService.list();
        return ResponseResult.success(bookmarks);
    }

    @GetMapping("/{id}")
    public ResponseResult<?> getBookmarkById(@PathVariable Long id) {
        Bookmark bookmark = bookmarkService.getById(id);
        if (bookmark != null) {
            return ResponseResult.success(bookmark);
        } else {
            return ResponseResult.fail(ResultCode.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseResult<?> createBookmark(@RequestBody Bookmark bookmark) {
        boolean success = bookmarkService.save(bookmark);
        if (success) {
            return ResponseResult.success("创建书签成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "创建书签失败");
        }
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        bookmark.setId(id);
        boolean success = bookmarkService.updateById(bookmark);
        if (success) {
            return ResponseResult.success("更新书签成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "更新书签失败");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteBookmark(@PathVariable Long id) {
        boolean success = bookmarkService.removeById(id);
        if (success) {
            return ResponseResult.success("删除书签成功");
        } else {
            return ResponseResult.fail(ResultCode.ERROR, "删除书签失败");
        }
    }
}