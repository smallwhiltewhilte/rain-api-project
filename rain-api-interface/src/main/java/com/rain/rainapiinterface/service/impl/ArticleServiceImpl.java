package com.rain.rainapiinterface.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rain.rainapiinterface.mapper.PoetryMapper;
import com.rain.rainapiinterface.model.dto.ArticleRequest;
import com.rain.rainapiinterface.model.enity.Poetry;
import com.rain.rainapiinterface.service.ArticleService;
import com.rain.rainapiinterface.service.PoetryService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Override
    public String getArticle(ArticleRequest articleRequest) {
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809));
        OkHttpClient okHttpClient = new OkHttpClient
                .Builder()
                .proxy(proxy)//自定义代理
                .addInterceptor(new OpenAiResponseInterceptor())//自定义返回值拦截
                .connectTimeout(10, TimeUnit.SECONDS)//自定义超时时间
                .writeTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .readTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .build();
        OpenAiClient openAiClient = OpenAiClient.builder()
                .apiKey(Arrays.asList("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"))
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(okHttpClient)
                //自己做了代理就传代理地址，没有可不不传
                .build();
        String content = generateContent(articleRequest.getWordCount(),articleRequest.getArticleThemes(),articleRequest.getArticleType());
        Message message = Message.builder().role(Message.Role.ASSISTANT).content(content).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
        return chatCompletionResponse.getChoices().get(0).getMessage().getContent();
    }

    private String generateContent(Integer wordCount, String articleThemes, String articleType) {
        return "帮我生成一篇"+wordCount+"字以"+articleThemes+"为主题的"+articleType;
    }
}




