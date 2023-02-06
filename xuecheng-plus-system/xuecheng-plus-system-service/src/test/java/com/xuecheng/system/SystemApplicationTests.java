package com.xuecheng.system;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.system.model.po.Dictionary;
import com.xuecheng.system.service.DictionaryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SystemApplicationTests {
   @Autowired
   private DictionaryService dictionaryService;



    @Test
    void testService() {
        PageParams pageParams = new PageParams();
        List<Dictionary> dictionaryList = dictionaryService.queryAll();
        System.out.println(dictionaryList);
    }

}
