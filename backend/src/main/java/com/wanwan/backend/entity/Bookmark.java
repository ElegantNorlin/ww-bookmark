package com.wanwan.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bookmark")
public class Bookmark extends BaseEntity {
    @TableField("user_id")
    private Long userId;

    @TableField("folder_id")
    private Long folderId;

    @TableField("title")
    private String title;

    @TableField("url")
    private String url;

    @TableField("icon")
    private String icon;

    @TableField("description")
    private String description;
}