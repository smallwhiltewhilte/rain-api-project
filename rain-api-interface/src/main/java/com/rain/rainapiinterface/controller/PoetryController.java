package com.rain.rainapiinterface.controller;

import com.rain.rainapiinterface.service.PoetryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 名称API
 */
@RestController
@RequestMapping("/poetry")
public class PoetryController {
    @Resource
    private PoetryService poetryService;
    @PostMapping("/verse")
    public String getVerse(@RequestBody com.rain.rainapiclientsdk.model.Poetry poetry) {
        return poetryService.getVerse(poetry.getAuthor(), poetry.getDynasty()).getVerse();
    }
}
