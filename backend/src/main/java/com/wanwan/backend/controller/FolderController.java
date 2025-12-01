package com.wanwan.backend.controller;

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
    public List<Folder> getAllFolders() {
        return folderService.list();
    }

    @GetMapping("/{id}")
    public Folder getFolderById(@PathVariable Long id) {
        return folderService.getById(id);
    }

    @PostMapping
    public boolean createFolder(@RequestBody Folder folder) {
        return folderService.save(folder);
    }

    @PutMapping("/{id}")
    public boolean updateFolder(@PathVariable Long id, @RequestBody Folder folder) {
        folder.setId(id);
        return folderService.updateById(folder);
    }

    @DeleteMapping("/{id}")
    public boolean deleteFolder(@PathVariable Long id) {
        return folderService.removeById(id);
    }
}