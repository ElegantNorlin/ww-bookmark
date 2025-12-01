package com.wanwan.backend.controller;

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
    public List<Bookmark> getAllBookmarks() {
        return bookmarkService.list();
    }

    @GetMapping("/{id}")
    public Bookmark getBookmarkById(@PathVariable Long id) {
        return bookmarkService.getById(id);
    }

    @PostMapping
    public boolean createBookmark(@RequestBody Bookmark bookmark) {
        return bookmarkService.save(bookmark);
    }

    @PutMapping("/{id}")
    public boolean updateBookmark(@PathVariable Long id, @RequestBody Bookmark bookmark) {
        bookmark.setId(id);
        return bookmarkService.updateById(bookmark);
    }

    @DeleteMapping("/{id}")
    public boolean deleteBookmark(@PathVariable Long id) {
        return bookmarkService.removeById(id);
    }
}