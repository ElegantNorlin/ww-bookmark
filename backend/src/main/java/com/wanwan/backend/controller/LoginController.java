package com.wanwan.backend.controller;

import com.wanwan.backend.common.ResultCode;
import com.wanwan.backend.common.ResponseResult;
import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.UserService;
import com.wanwan.backend.util.JwtUtil;
import com.wanwan.backend.util.RedisUtil;
import com.wanwan.backend.util.UserContext;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Resource
    private JwtUtil jwtUtil;
    
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private UserService userService;
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseResult<?> register(@RequestBody User user) {
        boolean success = userService.register(user);
        if (success) {
            return ResponseResult.success("注册成功");
        } else {
            return ResponseResult.fail(ResultCode.USER_EXIST);
        }
    }
    
    /**
     * 用户登录
     * @param loginRequest 登录请求，包含用户名和密码
     * @return 登录结果和JWT令牌
     */
    @PostMapping("/login")
    public ResponseResult<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        User user = userService.login(username, password);
        if (user != null) {
            // 生成访问令牌和刷新令牌
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);
            
            // 将访问令牌和刷新令牌存储到Redis中，设置过期时间
            redisUtil.set("access_token:" + username, accessToken, 30, TimeUnit.MINUTES);
            redisUtil.set("refresh_token:" + username, refreshToken, 7, TimeUnit.DAYS);
            
            // 将用户信息存储到ThreadLocal中
            UserContext.setUser(user);
            
            Map<String, Object> data = new HashMap<>();
            data.put("access_token", accessToken);
            data.put("refresh_token", refreshToken);
            data.put("user", user);
            
            return ResponseResult.success(data, "登录成功");
        } else {
            return ResponseResult.fail(ResultCode.USERNAME_PASSWORD_ERROR);
        }
    }
    
    /**
     * 刷新访问令牌
     * @param refreshRequest 刷新请求，包含refresh_token
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ResponseResult<?> refresh(@RequestBody Map<String, String> refreshRequest) {
        String refreshToken = refreshRequest.get("refresh_token");
        
        try {
            // 验证刷新令牌是否有效
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseResult.fail(ResultCode.TOKEN_INVALID);
            }
            
            // 从刷新令牌中获取用户名
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            // 检查Redis中是否存在该刷新令牌
            String storedRefreshToken = (String) redisUtil.get("refresh_token:" + username);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                return ResponseResult.fail(ResultCode.REFRESH_TOKEN_INVALID);
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(username);
            
            // 更新Redis中的访问令牌
            redisUtil.set("access_token:" + username, newAccessToken, 30, TimeUnit.MINUTES);
            
            Map<String, Object> data = new HashMap<>();
            data.put("access_token", newAccessToken);
            
            return ResponseResult.success(data, "令牌刷新成功");
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "令牌刷新失败");
        }
    }
    
    /**
     * 用户退出登录
     * @param token 请求头中的JWT令牌
     * @return 退出结果
     */
    @PostMapping("/logout")
    public ResponseResult<?> logout(@RequestHeader("Authorization") String token) {
        try {
            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // 从令牌中获取用户名
            String username = jwtUtil.getUsernameFromToken(token);
            
            // 从Redis中删除访问令牌和刷新令牌
            redisUtil.delete("access_token:" + username);
            redisUtil.delete("refresh_token:" + username);
            
            return ResponseResult.success("退出登录成功");
        } catch (Exception e) {
            return ResponseResult.fail(ResultCode.ERROR, "退出登录失败");
        }
    }
}