package com.wanwan.backend.service;

import com.wanwan.backend.entity.Bookmark;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface BookmarkService extends IService<Bookmark> {
    /**
     * 根据用户ID获取书签列表
     * @param userId 用户ID
     * @return 书签列表
     */
    List<Bookmark> getUserBookmarks(String userId);
}