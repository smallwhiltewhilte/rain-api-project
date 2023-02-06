package com.xuecheng.content.controller;

import com.xuecheng.content.model.dto.BindTeachPlanMediaDto;
import com.xuecheng.content.model.dto.TeachPlanDto;
import com.xuecheng.content.model.dto.SaveTeachPlanDto;
import com.xuecheng.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wangzan
 * @version 1.0
 * @description 课程计划编辑接口
 * @date 2023/1/19
 */
@Api(value = "课程计划编辑接口", tags = "课程计划编辑接口")
@Slf4j
@RestController
@RequestMapping("/teachplan")

public class TeachPlanController {
    @Autowired
    TeachPlanService teachPlanService;
    @ApiOperation("查询课程计划树形结构")
    @ApiImplicitParam(value = "courseId", name = "课程Id", required = true, dataType = "Long", paramType = "path")
    @GetMapping("/{courseId}/tree-nodes")
    public List<TeachPlanDto> getTreeNodes(@PathVariable Long courseId) {
        return teachPlanService.findTeachPlanTree(courseId);
    }
    @ApiOperation("保存课程计划")
    @ApiImplicitParam(value = "dto", name = "保存课程计划dto", required = true, dataType = "SaveTeachPlanDto", paramType = "body")
    @PostMapping()
    public void saveTeachPlan(@RequestBody SaveTeachPlanDto dto) {
        teachPlanService.saveTeachPlan(dto);
    }

    @ApiOperation("删除课程计划")
    @ApiImplicitParam(value = "teachPlanId", name = "课程计划id", required = true, dataType = "Long", paramType = "path")
    @DeleteMapping("/{teachPlanId}")
    public void deleteTeachPlan(@PathVariable Long teachPlanId) {
        teachPlanService.deleteTeachPlan(teachPlanId);
    }

    @ApiOperation("下移课程计划")
    @ApiImplicitParam(value = "teachPlanId", name = "课程计划id", required = true, dataType = "Long", paramType = "path")
    @PostMapping("/movedown/{teachPlanId}")
    public void moveDownTeachPlan(@PathVariable Long teachPlanId) {
        teachPlanService.moveDownTeachPlan(teachPlanId);
    }
    @ApiOperation("上移课程计划")
    @ApiImplicitParam(value = "teachPlanId", name = "课程计划id", required = true, dataType = "Long", paramType = "path")
    @PostMapping("/moveup/{teachPlanId}")
    public void moveUpTeachPlan(@PathVariable Long teachPlanId) {
        teachPlanService.moveUpTeachPlan(teachPlanId);
    }
    @ApiOperation(value = "课程计划和媒资信息绑定")
    @PostMapping("/association/media")
    void associationMedia(@RequestBody BindTeachPlanMediaDto bindTeachPlanMediaDto){
        teachPlanService.associationMedia(bindTeachPlanMediaDto);
    }

    @ApiOperation(value = "课程计划和媒资信息解除绑定")
    @DeleteMapping("/association/media/{teachPlanId}/{mediaId}")
    void delAssociationMedia(@PathVariable Long teachPlanId,@PathVariable Long mediaId){

    }

}
