package com.xuecheng.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.mapper.XcCourseTablesMapper;
import com.xuecheng.learning.model.dto.MyCourseTableItemDto;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.dto.XcChooseCourseDto;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.model.po.XcCourseTables;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 我的课程表service实现类
 * @date 2023/1/31
 */
@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Autowired
    XcChooseCourseMapper xcChooseCourseMapper;

    @Autowired
    XcCourseTablesMapper courseTablesMapper;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MyCourseTablesServiceImpl myCourseTablesService;

    @Override
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        //查询课程信息
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            XueChengPlusException.cast("课程信息不存在");
        }
        String charge = coursepublish.getCharge();
        XcChooseCourse xcChooseCourse = null;
        if ("201000".equals(charge)) {
            // 免费课程
            //添加到选课记录表，添加到我的课程表
            xcChooseCourse = addFreeCoruse(userId, coursepublish);
        } else {
            // 收费课程，只能添加到选课记录表
            xcChooseCourse = addChargeCoruse(userId, coursepublish);
        }
        //获取学习资格
        XcCourseTablesDto xcCourseTablesDto = getLeanringStatus(userId, courseId);
        String learnStatus = xcCourseTablesDto.getLearnStatus();
        // 构造返回对象
        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(xcChooseCourse, xcChooseCourseDto);
        xcChooseCourseDto.setLearnStatus(learnStatus);

        return xcChooseCourseDto;
    }


    public XcCourseTablesDto getLeanringStatus(String userId, Long courseId) {
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();
        if (xcCourseTables == null) {
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDto);
        LocalDateTime validtimeEnd = xcCourseTables.getValidtimeEnd();
        if (LocalDateTime.now().isAfter(validtimeEnd)) {
            return xcCourseTablesDto;
        }
        xcCourseTablesDto.setLearnStatus("702001");
        return xcCourseTablesDto;
    }

    public boolean saveChooseCourseStauts(String chooseCourseId) {
        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectById(chooseCourseId);
        if (xcChooseCourse != null) {
            String status = xcChooseCourse.getStatus();
            //待支付
            if ("701002".equals(status)) {
                //更新为选课成功
                xcChooseCourse.setStatus("701001");
                int update = xcChooseCourseMapper.updateById(xcChooseCourse);
                //添加到课程表
                addCourseTabls(xcChooseCourse);
                if (update > 0) {
                    log.debug("收到支付结果通知处理成功,选课记录:{}", xcChooseCourse);
                    return true;
                } else {
                    log.debug("收到支付结果通知处理失败,选课记录:{}", xcChooseCourse);
                    return false;
                }
            } else {
                log.debug("收到支付结果通知已经处理,选课记录:{}", xcChooseCourse);
                return true;
            }
        } else {
            log.debug("收到支付结果通知没有查询到关联的选课记录,chooseCourseId:{}", chooseCourseId);
        }
        return false;
    }


    /**
     * @param userId
     * @param courseId
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 根据课程和用户查询我的课程表中某一门课程
     * @date 2023/1/31
     */
    public XcCourseTables getXcCourseTables(String userId, Long courseId) {
        XcCourseTables xcCourseTables = courseTablesMapper.selectOne(new LambdaQueryWrapper<XcCourseTables>().eq(XcCourseTables::getUserId, userId).eq(XcCourseTables::getCourseId, courseId));
        return xcCourseTables;

    }


    @Transactional
    //添加免费课程,免费课程加入选课记录表、我的课程表
    public XcChooseCourse addFreeCoruse(String userId, CoursePublish coursepublish) {
        Long courseId = coursepublish.getId();
        // 校验该课程是否添加到了选课记录表，如果已添加则直接返回
        LambdaQueryWrapper<XcChooseCourse> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcChooseCourse::getCourseId, courseId);
        lambdaQueryWrapper.eq(XcChooseCourse::getUserId, userId);
        lambdaQueryWrapper.eq(XcChooseCourse::getOrderType, "700001");
        lambdaQueryWrapper.eq(XcChooseCourse::getStatus, "701001");
        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectOne(lambdaQueryWrapper);
        if (xcChooseCourse != null) {
            return xcChooseCourse;
        }
        // 向选课记录表添加记录
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700001");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(0f);
        chooseCourse.setValidDays(coursepublish.getValidDays());
        chooseCourse.setStatus("701001");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        xcChooseCourseMapper.insert(chooseCourse);
        // 添加到我的课程表
        addCourseTabls(chooseCourse);
        return chooseCourse;

    }

    @Transactional
    //添加收费课程
    public XcChooseCourse addChargeCoruse(String userId, CoursePublish coursepublish) {
        Long courseId = coursepublish.getId();
        // 校验该课程是否添加到了选课记录表，如果已添加则直接返回
        LambdaQueryWrapper<XcChooseCourse> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcChooseCourse::getCourseId, courseId);
        lambdaQueryWrapper.eq(XcChooseCourse::getUserId, userId);
        lambdaQueryWrapper.eq(XcChooseCourse::getOrderType, "700002");
        lambdaQueryWrapper.eq(XcChooseCourse::getStatus, "701002");
        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectOne(lambdaQueryWrapper);
        if (xcChooseCourse != null) {
            return xcChooseCourse;
        }
        // 向选课记录表添加记录
        XcChooseCourse chooseCourse = new XcChooseCourse();
        chooseCourse.setCourseId(courseId);
        chooseCourse.setCourseName(coursepublish.getName());
        chooseCourse.setUserId(userId);
        chooseCourse.setCompanyId(coursepublish.getCompanyId());
        chooseCourse.setOrderType("700002");
        chooseCourse.setCreateDate(LocalDateTime.now());
        chooseCourse.setCoursePrice(coursepublish.getPrice());
        chooseCourse.setValidDays(coursepublish.getValidDays());
        chooseCourse.setStatus("701002");
        chooseCourse.setValidtimeStart(LocalDateTime.now());
        chooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        xcChooseCourseMapper.insert(chooseCourse);
        return chooseCourse;
    }

    /**
     * @param xcChooseCourse 选课记录
     * @return com.xuecheng.learning.model.po.XcCourseTables
     * @description 添加到我的课程表
     * @date 2023/1/31
     */
    @Transactional
    public XcCourseTables addCourseTabls(XcChooseCourse xcChooseCourse) {
        String userId = xcChooseCourse.getUserId();
        Long courseId = xcChooseCourse.getCourseId();
        XcCourseTables xcCourseTables = getXcCourseTables(userId, courseId);
        if (xcCourseTables != null) {
            return xcCourseTables;
        }
        XcCourseTables courseTables = new XcCourseTables();
        // 选课记录id
        Long xcChooseCourseId = xcChooseCourse.getId();
        courseTables.setChooseCourseId(xcChooseCourseId);
        courseTables.setUserId(xcChooseCourse.getUserId());
        courseTables.setCourseId(xcChooseCourse.getCourseId());
        courseTables.setCompanyId(xcChooseCourse.getCompanyId());
        courseTables.setCourseName(xcChooseCourse.getCourseName());
        courseTables.setCreateDate(LocalDateTime.now());
        courseTables.setValidtimeStart(xcChooseCourse.getValidtimeStart());
        courseTables.setValidtimeEnd(xcChooseCourse.getValidtimeEnd());
        courseTables.setCourseType(xcChooseCourse.getOrderType());
        courseTablesMapper.insert(courseTables);

        return courseTables;

    }

    @Override
    public PageResult<XcCourseTables> myCourseTable(MyCourseTableParams params) {
        //页码
        long pageNo = params.getPage();
        //每页记录数,固定为4
        long pageSize = 4;
        //分页条件
        Page<XcCourseTables> page = new Page<>(pageNo, pageSize);
        // 拼装查询条件
        String userId = params.getUserId();
        LambdaQueryWrapper<XcCourseTables> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(XcCourseTables::getUserId, userId);
        // 收费类型
        lambdaQueryWrapper.eq(StringUtils.isNotEmpty(params.getCourseType()), XcCourseTables::getCourseType, params.getCourseType());
        if ("1".equals(params.getExpiresType())) {
            // 即将过期失效
            lambdaQueryWrapper.ge(XcCourseTables::getValidtimeEnd, LocalDateTime.now());
        } else if ("2".equals(params.getExpiresType())) {
            // 已过期
            lambdaQueryWrapper.le(XcCourseTables::getValidtimeEnd, LocalDateTime.now());
        }

        if ("1".equals(params.getSortType())) {
            // 按学习时间进行排序
            lambdaQueryWrapper.orderByAsc(XcCourseTables::getCreateDate);
        } else if ("2".equals(params.getSortType())) {
            // 按加入时间进行排序
            lambdaQueryWrapper.orderByAsc(XcCourseTables::getValidtimeStart);
        }
        //分页查询
        Page<XcCourseTables> pageResult = courseTablesMapper.selectPage(page, lambdaQueryWrapper);
        List<XcCourseTables> records = pageResult.getRecords();
        //记录总数
        long total = pageResult.getTotal();
        PageResult<XcCourseTables> courseTablesResult = new PageResult<>(records, total, pageNo, pageSize);


        return courseTablesResult;
    }

    public PageResult<MyCourseTableItemDto> mycourestabls(MyCourseTableParams params) {

        int page = params.getPage();
        int size = params.getSize();
        int startIndex = (page - 1) * size;
        params.setStartIndex(startIndex);

        List<MyCourseTableItemDto> myCourseTableItemDtos = courseTablesMapper.myCourseTables(params);
        int total = courseTablesMapper.myCourseTablesCount(params);


        PageResult pageResult = new PageResult();
        pageResult.setItems(myCourseTableItemDtos);
        pageResult.setCounts(total);
        pageResult.setPage(page);
        pageResult.setPageSize(size);
        return pageResult;

    }

}
