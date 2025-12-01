package com.wanwan.backend.controller;

import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.UserService;
import com.wanwan.backend.util.JwtUtil;
import com.wanwan.backend.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = userService.register(user);
        if (success) {
            response.put("success", true);
            response.put("message", "注册成功");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或邮箱已存在");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 用户登录
     * @param loginRequest 登录请求，包含用户名和密码
     * @return 登录结果和JWT令牌
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");
        
        Map<String, Object> response = new HashMap<>();
        
        User user = userService.login(username, password);
        if (user != null) {
            // 生成访问令牌和刷新令牌
            String accessToken = jwtUtil.generateAccessToken(username);
            String refreshToken = jwtUtil.generateRefreshToken(username);
            
            // 将访问令牌和刷新令牌存储到Redis中，设置过期时间
            redisUtil.set("access_token:" + username, accessToken, 30, TimeUnit.MINUTES);
            redisUtil.set("refresh_token:" + username, refreshToken, 7, TimeUnit.DAYS);
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("access_token", accessToken);
            response.put("refresh_token", refreshToken);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * 刷新访问令牌
     * @param refreshRequest 刷新请求，包含refresh_token
     * @return 新的访问令牌
     */
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody Map<String, String> refreshRequest) {
        String refreshToken = refreshRequest.get("refresh_token");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证刷新令牌是否有效
            if (!jwtUtil.validateToken(refreshToken)) {
                response.put("success", false);
                response.put("message", "无效的刷新令牌");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 从刷新令牌中获取用户名
            String username = jwtUtil.getUsernameFromToken(refreshToken);
            
            // 检查Redis中是否存在该刷新令牌
            String storedRefreshToken = (String) redisUtil.get("refresh_token:" + username);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                response.put("success", false);
                response.put("message", "刷新令牌已过期或无效");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
            // 生成新的访问令牌
            String newAccessToken = jwtUtil.generateAccessToken(username);
            
            // 更新Redis中的访问令牌
            redisUtil.set("access_token:" + username, newAccessToken, 30, TimeUnit.MINUTES);
            
            response.put("success", true);
            response.put("message", "令牌刷新成功");
            response.put("access_token", newAccessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "令牌刷新失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}