package com.rain.rainapiinterface.service;

import com.rain.rainapiinterface.domain.Poetry;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PoetryService extends IService<Poetry> {

    Poetry getVerse(String author, String dynasty);
}
