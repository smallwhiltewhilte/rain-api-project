package com.rain.rainapiinterface.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.rainapiinterface.model.enity.Poetry;
import com.rain.rainapiinterface.service.PoetryService;
import com.rain.rainapiinterface.mapper.PoetryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class PoetryServiceImpl extends ServiceImpl<PoetryMapper, Poetry> implements PoetryService {
    @Resource
    private PoetryMapper poetryMapper;

    @Override
    public Poetry getVerse(String author, String dynasty) {
        LambdaQueryWrapper<Poetry> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(author != null, Poetry::getAuthor, author);
        lambdaQueryWrapper.eq(dynasty != null, Poetry::getDynasty, dynasty);
        List<Poetry> poetryList = poetryMapper.selectList(lambdaQueryWrapper);
        return poetryList.get(RandomUtil.randomInt(poetryList.size()));
    }
}




