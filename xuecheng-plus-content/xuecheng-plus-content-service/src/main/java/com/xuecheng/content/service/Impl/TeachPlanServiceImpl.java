package com.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.content.mapper.TeachPlanMapper;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.po.TeachPlan;
import com.xuecheng.content.service.TeachPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author wangzan
 */
@Slf4j
@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachPlanMapper, TeachPlan> implements TeachPlanService {
    @Autowired
    TeachPlanMapper teachPlanMapper;

    @Override
    public List<TeachPlanDto> findTeachPlanTree(long courseId) {
        return teachPlanMapper.selectTreeNodes(courseId);
    }

    @Override
    public void saveTeachPlan(SaveTeachPlanDto dto) {
        Long id = dto.getId();
        TeachPlan teachPlan = teachPlanMapper.selectById(id);
        if (id == null) {
            teachPlan = new TeachPlan();
            BeanUtils.copyProperties(dto, teachPlan);
            //计算默认orderby
            int count = getTeachPlanCount(dto.getCourseId(), dto.getParentid());
            teachPlan.setOrderby(count + 1);
            teachPlanMapper.insert(teachPlan);
        } else {
            BeanUtils.copyProperties(dto, teachPlan);
            teachPlanMapper.updateById(teachPlan);
        }

    }

    // 计算课程计划orderby
    private int getTeachPlanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<TeachPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachPlan::getCourseId, courseId);
        queryWrapper.eq(TeachPlan::getParentid, parentId);
        return teachPlanMapper.selectCount(queryWrapper);
    }
}
