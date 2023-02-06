package com.xuecheng.content.controller;

import com.xuecheng.content.service.CoursePublishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author wangzan
 * @version 1.0
 * @description Freemarker生成静态页面接口
 * @date 2023/1/28
 */
@Controller
public class FreemarkerController {
    @Autowired
    CoursePublishService coursePublishService;
    @GetMapping("/testfreemarker")
public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        // 准备模型数据
        modelAndView.addObject("model","小明");
        // 设置视图的名称，模板文件的名称
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
