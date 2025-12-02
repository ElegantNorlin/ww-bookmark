package com.wanwan.backend.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("folder")
public class Folder extends BaseEntity {
    @TableField("user_id")
    private String userId;

    @TableField("parent_id")
    private String parentId;

    @TableField("ancestors")
    private String ancestors;

    @TableField("name")
    private String name;
}