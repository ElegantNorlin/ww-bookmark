package com.wanwan.backend.common;

import lombok.Getter;

/**
 * 响应状态码枚举类
 */
@Getter
public enum ResultCode {
    
    /**
     * 成功状态码
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 失败状态码
     */
    ERROR(500, "操作失败"),
    
    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),
    
    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 服务器内部错误
     */
    SERVER_ERROR(500, "服务器内部错误"),
    
    /**
     * 用户名或密码错误
     */
    USERNAME_PASSWORD_ERROR(40101, "用户名或密码错误"),
    
    /**
     * 用户已存在
     */
    USER_EXIST(40001, "用户已存在"),
    
    /**
     * 邮箱已存在
     */
    EMAIL_EXIST(40002, "邮箱已存在"),
    
    /**
     * 令牌无效
     */
    TOKEN_INVALID(40102, "令牌无效"),
    
    /**
     * 令牌过期
     */
    TOKEN_EXPIRED(40103, "令牌过期"),
    
    /**
     * 刷新令牌无效
     */
    REFRESH_TOKEN_INVALID(40104, "刷新令牌无效"),
    
    /**
     * 用户名不能为空
     */
    USERNAME_NOT_NULL(40003, "用户名不能为空"),
    
    /**
     * 密码不能为空
     */
    PASSWORD_NOT_NULL(40004, "密码不能为空"),
    
    /**
     * 邮箱不能为空
     */
    EMAIL_NOT_NULL(40005, "邮箱不能为空"),
    
    /**
     * 邮箱格式错误
     */
    EMAIL_FORMAT_ERROR(40006, "邮箱格式错误"),
    
    /**
     * 用户不存在
     */
    USER_NOT_EXIST(40401, "用户不存在"),
    
    /**
     * 文件夹已存在
     */
    FOLDER_EXIST(40007, "文件夹已存在"),
    
    /**
     * 文件夹不存在
     */
    FOLDER_NOT_EXIST(40402, "文件夹不存在");
    
    /**
     * 状态码
     */
    private final Integer code;
    
    /**
     * 消息
     */
    private final String message;
    
    /**
     * 构造函数
     * @param code 状态码
     * @param message 消息
     */
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}