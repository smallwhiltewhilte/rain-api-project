package com.xuecheng.checkcode.service.impl;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xuecheng.base.utils.EncryptUtil;
import com.xuecheng.checkcode.model.CheckCodeParamsDto;
import com.xuecheng.checkcode.model.CheckCodeResultDto;
import com.xuecheng.checkcode.service.AbstractCheckCodeService;
import com.xuecheng.checkcode.service.CheckCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author wangzan
 * @version 1.0
 * @description 手机验证码生成器
 * @date 2023/1/30
 */
@Service("PhoneCheckCodeService")
public class PhoneCheckCodeServiceImpl extends AbstractCheckCodeService implements CheckCodeService {
    @Resource(name="NumberLetterCheckCodeGenerator")
    @Override
    public void setCheckCodeGenerator(CheckCodeGenerator checkCodeGenerator) {
        this.checkCodeGenerator = checkCodeGenerator;
    }

    @Resource(name="UUIDKeyGenerator")
    @Override
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }


    @Resource(name="MemoryCheckCodeStore")
    @Override
    public void setCheckCodeStore(CheckCodeStore checkCodeStore) {
        this.checkCodeStore = checkCodeStore;
    }


    @Override
    public CheckCodeResultDto generate(CheckCodeParamsDto checkCodeParamsDto) {
        GenerateResult generate = generate(checkCodeParamsDto, 4, "checkcode:", 60);
        String key = generate.getKey();
        String code = generate.getCode();
        CheckCodeResultDto checkCodeResultDto = new CheckCodeResultDto();
        checkCodeResultDto.setKey(key);
        // 调用阿里云接口发送手机短信

        return checkCodeResultDto;
    }
}
