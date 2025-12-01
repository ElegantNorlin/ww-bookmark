package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.Bookmark;
import com.wanwan.backend.mapper.BookmarkMapper;
import com.wanwan.backend.service.BookmarkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class BookmarkServiceImpl extends ServiceImpl<BookmarkMapper, Bookmark> implements BookmarkService {
}