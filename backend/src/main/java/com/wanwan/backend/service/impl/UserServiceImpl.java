package com.wanwan.backend.service.impl;

import com.wanwan.backend.entity.User;
import com.wanwan.backend.mapper.UserMapper;
import com.wanwan.backend.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private BCryptPasswordEncoder passwordEncoder;
    
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            return false;
        }
        // 检查邮箱是否已存在
        if (getByEmail(user.getEmail()) != null) {
            return false;
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 设置创建时间和更新时间
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        // 保存用户
        return save(user);
    }
    
    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = getByUsername(username);
        // 检查用户是否存在，密码是否正确
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
    
    @Override
    public User getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }
    
    @Override
    public User getByEmail(String email) {
        return userMapper.selectByEmail(email);
    }
}