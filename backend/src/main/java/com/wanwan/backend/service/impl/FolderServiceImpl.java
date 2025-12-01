package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.Folder;
import com.wanwan.backend.mapper.FolderMapper;
import com.wanwan.backend.service.FolderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {
}