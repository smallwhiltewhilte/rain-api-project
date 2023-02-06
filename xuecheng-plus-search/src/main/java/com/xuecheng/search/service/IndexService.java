package com.xuecheng.search.service;

import com.xuecheng.search.po.CourseIndex;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程索引service
 * @dat2023/1/30
 */
public interface IndexService {

    /**
     * @param indexName 索引名称
     * @param id 主键
     * @param object 索引对象
     * @return Boolean true表示成功,false失败
     * @description 添加索引
     * @date 2023/1/30
     */
    public Boolean addCourseIndex(String indexName,String id,Object object);


    /**
     * @description 更新索引
     * @param indexName 索引名称
     * @param id 主键
     * @param object 索引对象
     * @return Boolean true表示成功,false失败
     * @date 2023/1/30
    */
    public Boolean updateCourseIndex(String indexName,String id,Object object);

    /**
     * @description 删除索引
     * @param indexName 索引名称
     * @param id  主键
     * @return java.lang.Boolean
     * @date 2023/1/30
    */
    public Boolean deleteCourseIndex(String indexName,String id);

}
