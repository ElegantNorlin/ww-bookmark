package com.wanwan.backend.interceptor;

import com.wanwan.backend.entity.User;
import com.wanwan.backend.service.UserService;
import com.wanwan.backend.util.JwtUtil;
import com.wanwan.backend.util.UserContext;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;
    
    @Resource
    private JwtUtil jwtUtil;

    /**
     * 请求处理前，从JWT令牌中提取用户信息并设置到ThreadLocal
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 清理可能残留的用户信息，确保请求隔离
        UserContext.clear();

        try {
            // 从请求头获取Authorization令牌
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                // 移除Bearer前缀
                token = token.substring(7);

                // 验证令牌
                if (jwtUtil.validateToken(token)) {
                    // 获取用户名
                    String username = jwtUtil.getUsernameFromToken(token);
                    if (username != null) {
                        // 根据用户名查询用户信息
                        User user = userService.getByUsername(username);
                        if (user != null) {
                            // 设置用户信息到ThreadLocal
                            UserContext.setUser(user);
                            log.debug("设置用户信息到ThreadLocal: {}", username);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("设置用户上下文失败: {}", e.getMessage());
            // 即使发生异常也不阻止请求继续，只是不设置用户信息
        }

        // 无论是否成功设置用户信息，都继续处理请求
        return true;
    }

    /**
     * 请求处理完成后，清理ThreadLocal中的用户信息，防止内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal中的用户信息
        UserContext.clear();
        log.debug("清理ThreadLocal中的用户信息");
    }
}
