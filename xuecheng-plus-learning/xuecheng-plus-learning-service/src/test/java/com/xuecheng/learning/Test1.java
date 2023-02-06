package com.xuecheng.learning;

import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.learning.feignclient.ContentServiceClient;
import com.xuecheng.learning.mapper.XcChooseCourseMapper;
import com.xuecheng.learning.model.dto.MyCourseTableItemDto;
import com.xuecheng.learning.model.dto.MyCourseTableParams;
import com.xuecheng.learning.model.po.XcChooseCourse;
import com.xuecheng.learning.service.MyCourseTablesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @description 测试
 * @author wangzan
 * @date 2023/1/31
 * @version 1.0
 */
 @SpringBootTest
public class Test1 {

  @Autowired
 ContentServiceClient contentServiceClient;

  @Autowired
    MyCourseTablesService myCourseTablesService;
  @Autowired
    XcChooseCourseMapper chooseCourseMapper;

  @Test
 public void test(){
   CoursePublish coursepublish = contentServiceClient.getCoursepublish(18L);
   System.out.println(coursepublish);
  }
  @Test
 public void test2(){
      MyCourseTableParams myCourseTableParams = new MyCourseTableParams();
      myCourseTableParams.setUserId("52");
      PageResult<MyCourseTableItemDto> mycourestabls = myCourseTablesService.mycourestabls(myCourseTableParams);
      System.out.println(mycourestabls);
  }

    @Test
    void testUpdate() {

        XcChooseCourse chooseCourse = chooseCourseMapper.selectById(20);
        chooseCourse.setStatus("701001");
        chooseCourseMapper.updateById(chooseCourse);
        System.out.println("success");
    }
}
