package com.rain.rainapiclientsdk.model;

import lombok.Data;

@Data
public class Article {
    private String articleId;
    private Integer wordCount;
    private String articleType;
    private String articleThemes;
}