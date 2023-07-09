package com.rain.rainapiinterface.model.enity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="poetry")
@Data
public class Poetry implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String dynasty;

    /**
     * 
     */
    private String verse;

    /**
     * 
     */
    private String author;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}