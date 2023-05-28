package com.rain.rainapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
/**
 * 接口信息
 */
@TableName(value = "interface_info")
@Data
public class InterfaceInfo implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 接口名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 请求类型
     */
    private String method;

    /**
     * 接口状态（）
     */
    private Integer status;
    /**
     * 请求参数
     */
    @TableField(value = "request_params")
    private String requestParams;
    /**
     * 请求头
     */
    @TableField(value = "request_header")
    private String requestHeader;

    /**
     * 响应头
     */
    @TableField(value = "response_header")
    private String responseHeader;

    /**
     *
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     *
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     *
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     *
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}