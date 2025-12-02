package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.Bookmark;
import com.wanwan.backend.mapper.BookmarkMapper;
import com.wanwan.backend.service.BookmarkService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {
    
    @Override
    public List<Bookmark> getUserBookmarks(String userId) {
        QueryWrapper<Bookmark> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        // 可以根据需要添加排序等条件
        queryWrapper.orderByDesc("create_time");
        return list(queryWrapper);
    }
}