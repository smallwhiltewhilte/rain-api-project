package com.xuecheng.content.service.jobhandler;

import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MessageProcessAbstract;
import com.xuecheng.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程发布任务
 * @date 2023/1/29
 */
@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {
    @Autowired
    CoursePublishService coursePublishService;
    //课程发布消息类型
    public static final String MESSAGE_TYPE = "course_publish";

    @XxlJob("CoursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("shardIndex=" + shardIndex + ",shardTotal=" + shardTotal);
        //参数:分片序号、分片总数、消息类型、一次最多取到的任务数量、一次任务调度执行的超时时间
        process(shardIndex, shardTotal, MESSAGE_TYPE, 5, 60);
    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        Long courseId = Long.valueOf(mqMessage.getBusinessKey1());
        log.debug("开始执行课程的发布任务，课程的id:{}", courseId);
        // 将课程信息进行静态化，上传静态页面到MinIO
        generateCourseHtml(mqMessage, courseId);
        //课程缓存
//        saveCourseCache(mqMessage,courseId);
        // 创建课程索引
        saveCourseIndex(mqMessage, courseId);

        return true;
    }

    // 添加索引库
    private void saveCourseIndex(MqMessage mqMessage, Long courseId) {
        //消息id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 判断任务是否完成
        int stageTwo = mqMessageService.getStageTwo(id);
        if (stageTwo > 0) {
            log.debug("当前阶段创建课程索引，已经完成不再处理，任务信息是:{}", courseId);
            return;
        }
        coursePublishService.saveCourseIndex(courseId);
        // 保存第二阶段状态
        mqMessageService.completedStageTwo(id);

    }

    // 课程静态化
    public void generateCourseHtml(MqMessage mqMessage, long courseId) {
        log.debug("开始进行课程静态化,课程id:{}", courseId);
        //消息id
        Long id = mqMessage.getId();
        MqMessageService mqMessageService = this.getMqMessageService();
        // 判断任务是否完成
        int stageOne = mqMessageService.getStageOne(id);
        if (stageOne > 0) {
            log.debug("课程静态化已处理直接返回，课程id:{}", courseId);
            return;
        }

        //生成静态文件
        File file = coursePublishService.generateCourseHtml(courseId);
        if (file == null) {
            XueChengPlusException.cast("课程静态化异常");
        }
        //上传html到MinIO

        coursePublishService.uploadCourseHtml(courseId, file);
        // 保存第一阶段状态
        mqMessageService.completedStageOne(id);
    }
}
