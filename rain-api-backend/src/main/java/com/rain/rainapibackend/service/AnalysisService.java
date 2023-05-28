package com.rain.rainapibackend.service;

import com.rain.rainapibackend.model.vo.InterfaceInfoCountVO;

import java.util.List;

public interface AnalysisService {

    List<InterfaceInfoCountVO> listTopInvokeInterfaceInfo();
}
