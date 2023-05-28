package com.rain.rainapibackend.model.vo;

import com.rain.rainapicommon.model.entity.InterfaceInfo;
import lombok.Data;

import java.io.Serializable;

/**
 * 接口信息封装视图
 */
@Data
public class InterfaceInfoCountVO extends InterfaceInfo implements Serializable {
    /**
     * 调用次数
     */
    private Integer invokeCount;

    private static final long serialVersionUID = 1L;
}
