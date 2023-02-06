package com.xuecheng.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.RestResponse;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.feignclient.MediaServiceClient;
import com.xuecheng.learning.mapper.XcLearnRecordMapper;
import com.xuecheng.learning.model.dto.XcCourseTablesDto;
import com.xuecheng.learning.model.po.XcLearnRecord;
import com.xuecheng.learning.service.LearningService;
import com.xuecheng.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 学习过程管理service实现类
 * @date 2023/1/31
 */
@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Autowired
    MediaServiceClient mediaServiceClient;

    @Autowired
    ContentServiceClient contentServiceClient;

    @Autowired
    MyCourseTablesService myCourseTablesService;

    @Autowired
    XcLearnRecordMapper learnRecordMapper;

    @Override
    public RestResponse<String> getVideo(String userId, Long courseId, Long teachplanId, String mediaId) {
        CoursePublish coursepublish = contentServiceClient.getCoursepublish(courseId);
        if (coursepublish == null) {
            XueChengPlusException.cast("课程信息不存在");
        }
        if (StringUtils.isNotEmpty(userId)) {
            XcCourseTablesDto leanringStatus = myCourseTablesService.getLeanringStatus(userId, courseId);
            // 学习资格
            String learnStatus = leanringStatus.getLearnStatus();
            if ("702001".equals(learnStatus)) {
                // 远程调用媒资获取视频播放地址
                return mediaServiceClient.getPlayUrlByMediaId(mediaId);
            } else if ("702003".equals(learnStatus)) {
                RestResponse.validfail("您的选课已过期需要申请或重新支付");
            }
        }
        // 用户未登录
        // 判断课程是否免费
        String charge = coursepublish.getCharge();
        if ("201000".equals(charge)){
            return mediaServiceClient.getPlayUrlByMediaId(mediaId);
        }

        return RestResponse.validfail("请购买课程后继续学习");
    }

    //保存学习记录
    public void saveLearnRecord(String userId, CoursePublish coursepublish, Long teachplanId) {

        //登录下保存学习记录
        if (StringUtils.isNotEmpty(userId)) {
            //课程id
            Long courseId = coursepublish.getId();
            //找到课程计划对应的名称
            String teachplanName = null;
            List<TeachPlanDto> teachplans = JSON.parseArray(coursepublish.getTeachplan(), TeachPlanDto.class);
            for (TeachPlanDto first : teachplans) {
                if (first.getTeachPlanTreeNodes() != null) {
                    for (TeachPlanDto second : first.getTeachPlanTreeNodes()) {
                        if (second.getId().equals(teachplanId)) {
                            teachplanName = second.getPname();
                            break;
                        }
                    }
                }
            }

            //初始化
            learnRecordMapper.initLearnRecord(userId, courseId, teachplanId);
            //更新学习记录
            XcLearnRecord learnRecord_u = new XcLearnRecord();
            learnRecord_u.setCourseName(coursepublish.getName());
            learnRecord_u.setLearnDate(LocalDateTime.now());
            learnRecord_u.setTeachplanName(teachplanName);
            int update = learnRecordMapper.update(learnRecord_u, new LambdaQueryWrapper<XcLearnRecord>().eq(XcLearnRecord::getUserId, userId).eq(XcLearnRecord::getCourseId, courseId).eq(XcLearnRecord::getTeachplanId, teachplanId));
            if (update > 0) {
                log.debug("更新学习记录,user:{},{}", userId, learnRecord_u);
            }

        }


    }

    //判断是不是试学课程
    private boolean isTeachplanPreview(Long teachplanId, List<TeachPlanDto> teachplans) {
        for (TeachPlanDto first : teachplans) {
            if (first.getTeachPlanTreeNodes() != null) {
                for (TeachPlanDto second : first.getTeachPlanTreeNodes()) {
                    if (second.getId().equals(teachplanId)) {
                        if (second.getIsPreview().equals("1")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}
