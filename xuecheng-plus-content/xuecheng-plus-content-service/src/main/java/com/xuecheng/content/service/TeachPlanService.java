package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.po.TeachPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xuecheng.content.model.po.TeachPlanMedia;

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
     * @param courseId
     * @return java.util.List<com.xuecheng.content.model.dto.TeachPlanDto>
     * @description 得到课程计划树
     * @date 2023/1/19
     **/
    List<TeachPlanDto> findTeachPlanTree(long courseId);

    /**
     * @param dto
     * @return void
     * @description 保存课程计划(新增 / 修改)
     * @date 2023/1/19
     **/
    void saveTeachPlan(SaveTeachPlanDto dto);

    /**
     * @param teachPlanId 课程计划id
     * @return void
     * @description 删除课程计划
     * @date 2023/1/28
     **/
    void deleteTeachPlan(Long teachPlanId);

    /**
     * @return void
     * @description 下移课程计划
     * @date 2023/1/28
     * @param    teachPlanId    课程计划id
     **/
    void moveDownTeachPlan(Long teachPlanId);

    /**
     * @return void
     * @description 上移课程计划
     * @date 2023/1/28
     * @param    teachPlanId    课程计划id
     **/
    void moveUpTeachPlan(Long teachPlanId);
/**
 * @description 教学计划绑定媒资
 * @date 2023/1/28
 * @param	bindTeachplanMediaDto
 * @return com.xuecheng.content.model.po.TeachPlanMedia
 **/
     TeachPlanMedia associationMedia(BindTeachPlanMediaDto bindTeachplanMediaDto);
}
