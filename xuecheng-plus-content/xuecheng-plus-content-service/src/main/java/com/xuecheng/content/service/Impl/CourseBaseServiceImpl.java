package com.xuecheng.content.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseMarketService;
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
 * @description 课程基本信息 服务实现类
 * @date 2023/1/17
 */
@Slf4j
@Service
public class CourseBaseServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseMarketService courseMarketService;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(Long companyId, PageParams params, QueryCourseParamsDto queryCourseParamsDto) {

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //拼接查询条件
        //根据课程名称模糊查询  name like '%名称%'
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());

        //根据课程审核状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());

        //根据课程发布状态
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        queryWrapper.eq(CourseBase::getCompanyId, companyId);

        //分页参数
        Page<CourseBase> page = new Page<>(params.getPageNo(), params.getPageSize());


        //分页查询E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);

        List<CourseBase> items = pageResult.getRecords();
        long total = pageResult.getTotal();


        //准备返回数据 List<T> items, long counts, long page, long pageSize
        return new PageResult<>(items, total, params.getPageNo(), params.getPageSize());
    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
//        // 对参数进行合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
////            throw new RuntimeException("课程名称为空");
//            XueChengPlusException.cast("课程名称为空");
//        }
//
//        if (StringUtils.isBlank(dto.getMt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getSt())) {
//            throw new RuntimeException("课程分类为空");
//        }
//
//        if (StringUtils.isBlank(dto.getGrade())) {
//            throw new RuntimeException("课程等级为空");
//        }
//
//        if (StringUtils.isBlank(dto.getTeachmode())) {
//            throw new RuntimeException("教育模式为空");
//        }
//
//        if (StringUtils.isBlank(dto.getUsers())) {
//            throw new RuntimeException("适应人群为空");
//        }
//
//        if (StringUtils.isBlank(dto.getCharge())) {
//            throw new RuntimeException("收费规则为空");
//        }

        //对数据进行封装，然后持久化
        CourseBase courseBase = new CourseBase();
        // 将传入dto数据设置到courseBase对象
        BeanUtils.copyProperties(dto, courseBase);
        // 设置机构id
        courseBase.setCompanyId(companyId);
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());
        // 审核状态默认为未提交
        courseBase.setAuditStatus("202002");
        // 发布状态默认为未发布
        courseBase.setStatus("203001");

        // 向课程基本表插入一条记录
        int insert = courseBaseMapper.insert(courseBase);
        //获取课程id
        Long courseId = courseBase.getId();
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        courseMarket.setId(courseId);

        // 校验如果课程收费，则必须要有价格
        int i = saveCourseMarket(courseMarket);
        // 向课程营销表插入一条记录
        if (insert <= 0 || i <= 0) {
            throw new RuntimeException("添加课程失败");
        }
        // 组装要返回的结果
        return getCourseBaseInfo(courseId);
    }

    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);

        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }

        // 根据课程分类id查询分类名称
        String mt = courseBase.getMt();
        String st = courseBase.getSt();
        CourseCategory mtCategory = courseCategoryMapper.selectById(mt);
        CourseCategory stCategory = courseCategoryMapper.selectById(st);
        if (mtCategory != null) {
            //分类名称
            courseBaseInfoDto.setMtName(mtCategory.getName());
        }
        if (stCategory != null) {
            //分类名称
            courseBaseInfoDto.setStName(stCategory.getName());
        }
        return courseBaseInfoDto;
    }

    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto editCourseDto) {
        //校验
        Long id = editCourseDto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(id);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }
        // 校验本机构只能修改本机构信息
        if (courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("本机构只能修改本机构的信息");
        }

        // 封装基本信息的数据
        BeanUtils.copyProperties(editCourseDto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());
        // 更新课程基本信息
        courseBaseMapper.updateById(courseBase);
        // 封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(editCourseDto, courseMarket);

        // 校验如果课程收费，则必须要有价格
        saveCourseMarket(courseMarket);

        // 查询课程信息
        return getCourseBaseInfo(id);
    }

    @Override
    public void deleteCourseBase(Long courseId) {
        courseBaseMapper.deleteById(courseId);
    }

    private int saveCourseMarket(CourseMarket courseMarket) {
        String charge = courseMarket.getCharge();
        if (StringUtils.isEmpty(charge)) {
            XueChengPlusException.cast("课程收费规则没有选择");
        }
        if (charge.equals("201001")) {
            if (courseMarket.getPrice() == null || courseMarket.getPrice().floatValue() <= 0) {
                XueChengPlusException.cast("课程设置了收费价格不能为空且必须大于0");
            }
        }
        // 保存
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b ? 1 : 0;
    }

}
