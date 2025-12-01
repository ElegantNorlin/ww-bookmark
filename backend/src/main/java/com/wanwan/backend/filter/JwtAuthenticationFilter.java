package com.wanwan.backend.filter;

import com.wanwan.backend.util.JwtUtil;
import com.wanwan.backend.util.RedisUtil;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Resource
    private JwtUtil jwtUtil;
    
    @Resource
    private UserDetailsService userDetailsService;
    
    @Resource
    private RedisUtil redisUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 获取Authorization头
        String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        // 检查Authorization头是否包含Bearer前缀
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取JWT令牌
            jwt = authorizationHeader.substring(7);
            
            // 尝试从JWT令牌中获取用户名
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
            } catch (Exception e) {
                // 令牌可能已过期，但我们先继续执行，后续会处理
            }
        }
        
        // 检查用户名是否存在，且SecurityContext中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 检查Redis中是否存在该访问令牌
            String storedAccessToken = (String) redisUtil.get("access_token:" + username);
            
            // 加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // 验证JWT令牌是否有效，且与Redis中存储的令牌一致
            if (jwtUtil.validateToken(jwt) && jwt.equals(storedAccessToken)) {
                // 创建认证令牌
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置认证信息到SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}