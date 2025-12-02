package com.wanwan.backend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应封装类
 * @param <T> 响应数据类型
 */
@Data
public class ResponseResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private long timestamp;
    
    /**
     * 无参构造函数
     */
    public ResponseResult() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 构造函数
     * @param code 状态码
     * @param message 消息
     * @param data 响应数据
     */
    public ResponseResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功响应，返回数据
     * @param data 数据
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    
    /**
     * 成功响应，返回数据和自定义消息
     * @param data 数据
     * @param message 自定义消息
     * @param <T> 数据类型
     * @return 响应结果
     */
    public static <T> ResponseResult<T> success(T data, String message) {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), message, data);
    }
    
    /**
     * 成功响应，不带数据
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }
    
    /**
     * 失败响应
     * @param code 状态码
     * @param message 错误消息
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(Integer code, String message) {
        return new ResponseResult<>(code, message, null);
    }
    
    /**
     * 失败响应，使用枚举状态码
     * @param resultCode 状态码枚举
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(ResultCode resultCode) {
        return new ResponseResult<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
    
    /**
     * 失败响应，自定义消息
     * @param resultCode 状态码枚举
     * @param message 自定义消息
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(ResultCode resultCode, String message) {
        return new ResponseResult<>(resultCode.getCode(), message, null);
    }
}