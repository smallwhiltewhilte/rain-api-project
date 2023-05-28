package com.rain.rainapibackend.controller;

import com.rain.rainapibackend.annotation.AuthCheck;
import com.rain.rainapibackend.common.BaseResponse;
import com.rain.rainapibackend.common.ResultUtils;
import com.rain.rainapibackend.constant.UserConstant;
import com.rain.rainapibackend.model.vo.InterfaceInfoCountVO;
import com.rain.rainapibackend.service.AnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 分析接口
 */
@RestController
@RequestMapping("/analysis")
@Slf4j
public class AnalysisController {
    @Resource
    private AnalysisService analysisService;

    @GetMapping("/top/interface/invoke")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<InterfaceInfoCountVO>> listTopInvokeInterfaceInfo() {
        return ResultUtils.success(analysisService.listTopInvokeInterfaceInfo());
    }
}
