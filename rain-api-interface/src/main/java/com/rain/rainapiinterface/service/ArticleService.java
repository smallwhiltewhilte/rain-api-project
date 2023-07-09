package com.rain.rainapiinterface.service;


import com.rain.rainapiinterface.model.dto.ArticleRequest;

public interface ArticleService {

    String getArticle(ArticleRequest articleRequest);
}
