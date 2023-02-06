package com.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.mapper.TeachPlanMapper;
import com.xuecheng.content.mapper.TeachPlanMediaMapper;
import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.model.po.TeachPlan;
import com.xuecheng.content.model.po.TeachPlanMedia;
import com.xuecheng.content.service.TeachPlanService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程计划 服务实现类
 * @date 2023/1/17
 */
@Slf4j
@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachPlanMapper, TeachPlan> implements TeachPlanService {
    @Autowired
    TeachPlanMapper teachPlanMapper;
    @Autowired
    TeachPlanMediaMapper teachPlanMediaMapper;

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

    @Override
    public void deleteTeachPlan(Long teachPlanId) {
        LambdaQueryWrapper<TeachPlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TeachPlan::getParentid, teachPlanId);
        // 查询该课程计划是否有子课程计划
        List<TeachPlan> teachPlanList = teachPlanMapper.selectList(lambdaQueryWrapper);
        if (teachPlanList == null || teachPlanList.size() == 0) {
            // 没有则可直接删除
            teachPlanMapper.deleteById(teachPlanId);
        } else {
            // 有则返回无法删除
            XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
        }
    }

    @Transactional
    @Override
    public void moveDownTeachPlan(Long teachPlanId) {
        moveTeachPlan(teachPlanId, true);
    }

    private void moveTeachPlan(Long teachPlanId, boolean isDown) {
        TeachPlan teachPlan = teachPlanMapper.selectById(teachPlanId);
        if (teachPlan == null) {
            XueChengPlusException.cast("教学计划不存在");
        }
        LambdaQueryWrapper<TeachPlan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        Integer grade = teachPlan.getGrade();
        if (grade == 1) {
            lambdaQueryWrapper.eq(TeachPlan::getCourseId, teachPlan.getCourseId());
        } else if (grade == 2) {
            lambdaQueryWrapper.eq(TeachPlan::getParentid, teachPlan.getParentid());
        }
        lambdaQueryWrapper.eq(TeachPlan::getGrade, teachPlan.getGrade());
        TeachPlan exchangeTeachPlan = null;
        if (isDown) {
            lambdaQueryWrapper.gt(TeachPlan::getOrderby, teachPlan.getOrderby());
            List<TeachPlan> teachPlanList = teachPlanMapper.selectList(lambdaQueryWrapper);
            if (teachPlanList == null || teachPlanList.size() == 0) {
                return;
            }
            exchangeTeachPlan = teachPlanList.get(0);
        } else {
            lambdaQueryWrapper.lt(TeachPlan::getOrderby, teachPlan.getOrderby());
            List<TeachPlan> teachPlanList = teachPlanMapper.selectList(lambdaQueryWrapper);
            if (teachPlanList == null || teachPlanList.size() == 0) {
                return;
            }
            exchangeTeachPlan = teachPlanList.get(teachPlanList.size() - 1);
        }
        Integer order = teachPlan.getOrderby();
        teachPlan.setOrderby(exchangeTeachPlan.getOrderby());
        exchangeTeachPlan.setOrderby(order);
        teachPlanMapper.updateById(teachPlan);
        teachPlanMapper.updateById(exchangeTeachPlan);
    }

    @Transactional
    @Override
    public void moveUpTeachPlan(Long teachPlanId) {
        moveTeachPlan(teachPlanId, false);
    }

    @Transactional
    @Override
    public TeachPlanMedia associationMedia(BindTeachPlanMediaDto bindTeachplanMediaDto) {
        Long teachPlanId = bindTeachplanMediaDto.getTeachplanId();
        TeachPlan teachPlan = teachPlanMapper.selectById(teachPlanId);
        // 约束校验

        // 教学计划不存在无法绑定
        if (teachPlan == null) {

        }
        // 只有二级目录才可以绑定视频
        Integer grade = teachPlan.getGrade();
        if (grade != 2) {
            XueChengPlusException.cast("只有二级目录才能绑定视频");
        }
        // 删除原来的绑定关系
        LambdaQueryWrapper<TeachPlanMedia> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(TeachPlanMedia::getTeachplanId,teachPlanId);
        teachPlanMediaMapper.delete(lambdaQueryWrapper);

        // 添加新纪录
        TeachPlanMedia teachPlanMedia = new TeachPlanMedia();
        teachPlanMedia.setTeachplanId(teachPlanId);
        teachPlanMedia.setMediaFilename(bindTeachplanMediaDto.getFileName());
        teachPlanMedia.setMediaId(bindTeachplanMediaDto.getMediaId());
        teachPlanMedia.setCreateDate(LocalDateTime.now());
        teachPlanMedia.setCourseId(teachPlan.getCourseId());
        teachPlanMediaMapper.insert(teachPlanMedia);
        return teachPlanMedia;
    }

    // 计算课程计划orderby
    private int getTeachPlanCount(Long courseId, Long parentId) {
        LambdaQueryWrapper<TeachPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TeachPlan::getCourseId, courseId);
        queryWrapper.eq(TeachPlan::getParentid, parentId);
        return teachPlanMapper.selectCount(queryWrapper);
    }
}
