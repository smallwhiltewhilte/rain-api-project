package com.xuecheng.content.service.Impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangzan
 * @version 1.0
 * @description TODO
 * @date 2023/1/17
 */
@Slf4j
@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {
    @Autowired
    CourseCategoryMapper courseCategoryMapper;
    private static final String id="1";
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes() {
        // 得到根节点下属的所有节点
        List<CourseCategoryTreeDto> categoryTreeDtos = courseCategoryMapper.selectTreeNodes(id);
        // 将数据封装到List中，只包含根节点的直接下属节点
        List<CourseCategoryTreeDto> courseCategoryTreeDtos = new ArrayList<>();
        Map<String, CourseCategoryTreeDto> nodeMap = new HashMap<>();
        categoryTreeDtos.stream().forEach(item -> {
                    nodeMap.put(item.getId(), item);

                    if (item.getParentid().equals(id)) {
                        courseCategoryTreeDtos.add(item);
                    }
                    CourseCategoryTreeDto parentNode = nodeMap.get(item.getParentid());
                    if (parentNode != null) {
                        if (parentNode.getChildrenTreeNodes() == null) {
                            parentNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                        }
                        // 找到子节点，将其放到其父节点的childrenTreeNodes
                        parentNode.getChildrenTreeNodes().add(item);
                    }
                }

        );
        return courseCategoryTreeDtos;
    }
}
