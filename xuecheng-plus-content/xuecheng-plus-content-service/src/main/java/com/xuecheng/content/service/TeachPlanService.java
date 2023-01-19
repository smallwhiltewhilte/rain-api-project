package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.po.TeachPlan;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author wangzan
 * @since 2023-01-17
 */
public interface TeachPlanService extends IService<TeachPlan> {
    /**
     * @description 得到课程计划树
     * @date 2023/1/19 12:53
     * @param	courseId
     * @return java.util.List<com.xuecheng.content.model.dto.TeachPlanDto>
     **/
    List<TeachPlanDto> findTeachPlanTree(long courseId);
    /**
     * @description 保存课程计划(新增/修改)
     * @date 2023/1/19 12:53
     * @param	dto
     * @return void
     **/
    void saveTeachPlan(SaveTeachPlanDto dto);
}
