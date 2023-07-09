package com.rain.rainapiinterface.model.dto;

import lombok.Data;

@Data
public class ArticleRequest {
    private String articleId;
    private Integer wordCount;
    private String articleType;
    private String articleThemes;
}
