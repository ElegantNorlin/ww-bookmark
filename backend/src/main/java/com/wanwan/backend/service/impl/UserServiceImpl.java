package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.User;
import com.wanwan.backend.mapper.UserMapper;
import com.wanwan.backend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}