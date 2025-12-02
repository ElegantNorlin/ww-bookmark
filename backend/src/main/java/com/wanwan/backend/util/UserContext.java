package com.wanwan.backend.util;

import com.wanwan.backend.entity.User;

/**
 * 用户上下文工具类
 * 用于在ThreadLocal中存储和获取当前登录用户信息
 */
public class UserContext {
    
    // 存储用户信息的ThreadLocal对象
    private static final ThreadLocal<User> USER_HOLDER = new ThreadLocal<>();
    
    /**
     * 设置当前线程的用户信息
     * @param user 用户对象
     */
    public static void setUser(User user) {
        USER_HOLDER.set(user);
    }
    
    /**
     * 获取当前线程的用户信息
     * @return 用户对象，如果没有设置则返回null
     */
    public static User getUser() {
        return USER_HOLDER.get();
    }
    
    /**
     * 获取当前线程的用户信息（别名方法，方便使用）
     * @return 用户对象，如果没有设置则返回null
     */
    public static User getCurrentUser() {
        return getUser();
    }
    
    /**
     * 获取当前线程的用户ID
     * @return 用户ID，如果没有设置则返回null
     */
    public static Long getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }
    
    /**
     * 获取当前线程的用户名
     * @return 用户名，如果没有设置则返回null
     */
    public static String getUsername() {
        User user = getUser();
        return user != null ? user.getUsername() : null;
    }
    
    /**
     * 清理当前线程的用户信息
     * 避免ThreadLocal内存泄漏
     */
    public static void clear() {
        USER_HOLDER.remove();
    }
    
    /**
     * 判断当前线程是否已设置用户信息
     * @return 是否存在用户信息
     */
    public static boolean hasUser() {
        return getUser() != null;
    }
}