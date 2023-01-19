package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.TeachPlan;
import com.xuecheng.content.model.po.TeachPlanMedia;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程计划dto
 * @date 2023/1/19
 */
@Data
@ToString
public class TeachPlanDto extends TeachPlan {
    // 关联的媒资信息
    TeachPlanMedia teachPlanMedia;
    //子目录
    List<TeachPlanDto>teachPlanTreeNodes;
}
