package com.rain.rainapicommon.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户调用接口关系表
 *
 * @TableName user_interface_info
 */
@TableName(value = "user_interface_info")
@Data
public class UserInterfaceInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 调用用户 id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 接口 id
     */
    @TableField(value = "interface_info_id")
    private Long interfaceInfoId;

    /**
     * 总调用次数
     */
    @TableField(value = "total_num")
    private Integer totalNum;

    /**
     * 剩余调用次数
     */
    @TableField(value = "left_num")
    private Integer leftNum;

    /**
     * 接口状态（0-关闭，1-开启）
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 是否删除（0-未删，1-删除）
     */
    @TableField(value = "is_delete")
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}