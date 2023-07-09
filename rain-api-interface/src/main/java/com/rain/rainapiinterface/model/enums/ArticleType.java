package com.rain.rainapiinterface.model.enums;

public enum ArticleType {
    BLOG("技术博客"),
    POETRY("诗歌"),
    DRAMA("戏剧"),
    FICTION("小说"),
    PROSE("散文"),
    BIOGRAPHY("传记"),
    COMMENTS("评论"),
    ADVERTISING_COPY("广告文案"),
    NEWS_REPORTS("新闻报道"),
    SCREENPLAY("电影剧本"),
    HUMOROUS_JOKES("幽默段子"),
    TRAVEL_DIARY("旅行日记")
    ;
    private final String type;

    ArticleType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
