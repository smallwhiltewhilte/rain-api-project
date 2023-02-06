package com.xuecheng.content.service.Impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.feignclient.model.CourseIndex;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.CourseTeacherDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.CourseTeacherService;
import com.xuecheng.content.service.TeachPlanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程发布service实现类
 * @date 2023/1/28
 */
@Slf4j
@Service
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;
    @Autowired
    TeachPlanService teachPlanService;
    @Autowired
    CourseTeacherService courseTeacherService;
    @Autowired
    MediaServiceClient mediaServiceClient;
    @Autowired
    SearchServiceClient searchServiceClient;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    MqMessageService mqMessageService;
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;
    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;
    @Autowired
    CoursePublishMapper coursePublishMapper;
    public static final String MESSAGE_TYPE = "course_publish";

    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {

        // 基本信息、营销信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        // 教学计划
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        // 课程教师
        List<CourseTeacherDto> courseTeacherDtos = courseTeacherService.getCourseTeacher(courseId);
        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachPlans(teachPlanTree);
        coursePreviewDto.setCourseTeachers(courseTeacherDtos);
        return coursePreviewDto;
    }

    @Override
    public void commitAudit(Long companyId, Long courseId) {
        //约束校验
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //课程审核状态
        String auditStatus = courseBase.getAuditStatus();
        //当前审核状态为已提交不允许再次提交
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("当前为等待审核状态，审核完成可以再次提交。");
        }
        //本机构只允许提交本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其它机构的课程。");
        }

        //课程图片是否填写
        if (StringUtils.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("提交失败，请上传课程图片");
        }
        List<TeachPlanDto> teachPlanTree = teachPlanService.findTeachPlanTree(courseId);
        if (teachPlanTree.size() <= 0) {
            XueChengPlusException.cast("提交失败，没有添加课程计划！");
        }
        // 封装数据，基本信息、营销信息、课程计划信息、师资信息
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        // 查询基本信息
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        // 营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //转为json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //将课程营销信息json数据放入课程预发布表
        coursePublishPre.setMarket(courseMarketJson);

        //查询课程计划信息
        coursePublishPre.setStatus("202003");
        if (coursePublishPreMapper.selectById(courseId) == null) {
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }
        //更新课程基本表的审核状态
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);

    }

    @Transactional
    @Override
    public void coursePublish(Long courseId, Long companyId) {
        // 约束校验

        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("请先提交课程审核，审核通过才可以发布");
        }
        //本机构只允许提交本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不允许提交其它机构的课程。");
        }


        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if (!"202004".equals(auditStatus)) {
            XueChengPlusException.cast("操作失败，课程审核通过方可发布。");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

    }

    @Override
    public File generateCourseHtml(Long courseId) {
        File htmlFile = null;
        try {
            //配置freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //加载模板
            //选指定模板路径,classpath下templates下
            //得到classpath路径
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //设置字符编码
            configuration.setDefaultEncoding("utf-8");

            //指定模板文件名称
            Template template = configuration.getTemplate("course_template.ftl");

            //准备数据
            CoursePreviewDto coursePreviewInfo = getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //静态化
            //参数1：模板，参数2：数据模型
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //将静态化内容输出到文件中
            InputStream inputStream = IOUtils.toInputStream(content);
            //创建静态化文件
            htmlFile = File.createTempFile("course", ".html");
            log.debug("课程静态化，生成静态文件:{}", htmlFile.getAbsolutePath());
            //输出流
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
        return htmlFile;
    }

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String result = mediaServiceClient.uploadFile(multipartFile, "course", courseId + ".html");
        if (result == null) {
            XueChengPlusException.cast("远程调用媒资服务上传文件失败");
        }
    }

    @Override
    public Boolean saveCourseIndex(Long courseId) {

        // 查询课程发布数据
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if (coursePublish == null) {
            XueChengPlusException.cast("课程发布信息为空");
        }
        // 组装数据
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        // 远程调用搜索服务创建索引
        Boolean result = searchServiceClient.add(courseIndex);
        if (!result) {
            XueChengPlusException.cast("创建课程索引失败");
        }
        return result;
    }

    @Override
    public CoursePublish getCoursePublish(Long courseId) {
        return coursePublishMapper.selectById(courseId);
    }

    /*@Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        String json = stringRedisTemplate.opsForValue().get("course:" + courseId);
        if (StringUtils.isNotEmpty(json)) {
            if ("null".equals(json)) {
                return null;
            }
            CoursePublish coursePublish = JSON.parseObject(json, CoursePublish.class);
            return coursePublish;
        } else {
            synchronized (this) {
                // 再次查询缓存
                json = stringRedisTemplate.opsForValue().get("course:" + courseId);
                if (StringUtils.isNotEmpty(json)) {
                    CoursePublish coursePublish = JSON.parseObject(json,CoursePublish.class);
                    return coursePublish;
                }

                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                System.out.println("查询数据库...");
//            if (coursePublish != null) {
                stringRedisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish), 300, TimeUnit.SECONDS);
//            }
                return coursePublish;
            }
        }
    }*/

    // setnx 分布式锁
    /*@Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        String json = stringRedisTemplate.opsForValue().get("course:" + courseId);
        if (StringUtils.isNotEmpty(json)) {
            if ("null".equals(json)) {
                return null;
            }
            CoursePublish coursePublish = JSON.parseObject(json, CoursePublish.class);
            return coursePublish;
        } else {
            Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent("lock:" + courseId, String.valueOf(courseId),300,TimeUnit.SECONDS);
            try {
                if(lock) {
                    // 再次查询缓存
                    json = stringRedisTemplate.opsForValue().get("course:" + courseId);
                    if (StringUtils.isNotEmpty(json)) {
                        CoursePublish coursePublish = JSON.parseObject(json,CoursePublish.class);
                        return coursePublish;
                    }

                    CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                    System.out.println("查询数据库...");
                    stringRedisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish), 300, TimeUnit.SECONDS);
                    return coursePublish;
                }
            } finally {
                // 删除锁
            }
        }
    }*/
    @Override
    public CoursePublish getCoursePublishCache(Long courseId) {
        String json = stringRedisTemplate.opsForValue().get("course:" + courseId);
        if (StringUtils.isNotEmpty(json)) {
            if ("null".equals(json)) {
                return null;
            }
            return JSON.parseObject(json, CoursePublish.class);
        } else {
            RLock lock = redissonClient.getLock("lock:course:" + courseId);
            lock.lock();
            try {
                // 再次查询缓存
                json = stringRedisTemplate.opsForValue().get("course:" + courseId);
                if (StringUtils.isNotEmpty(json)) {
                    return JSON.parseObject(json,CoursePublish.class);
                }
                CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
                stringRedisTemplate.opsForValue().set("course:" + courseId, JSON.toJSONString(coursePublish), 7, TimeUnit.DAYS);
                return coursePublish;
            } finally {
                // 释放锁
                lock.unlock();
            }
        }
    }

    @Override
    public Boolean saveCourseCache(Long courseId) {
        return null;
    }

    //保存消息表
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage(MESSAGE_TYPE, String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            XueChengPlusException.cast("添加消息记录失败");
        }
    }

    //保存课程发布信息
    private void saveCoursePublish(Long courseId) {
        // 来源于预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("课程预发布数据为空");
        }
        CoursePublish coursePublish = new CoursePublish();
        // 拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        // 已发布
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishMapper.insert(coursePublish);
        } else {
            coursePublishMapper.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

    }

}
