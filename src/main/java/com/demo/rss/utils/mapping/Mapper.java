package com.demo.rss.utils.mapping;

import com.demo.rss.dto.Article;
import com.demo.rss.dto.RSSItem;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public class Mapper {
    public static Article mapRSSItem2Article(@NotNull RSSItem rssItem) {
        final Article article = new Article();
        article.setTitle(rssItem.getTitle());
        article.setLink(rssItem.getLink());
        article.setAuthor(rssItem.getAuthor());
        article.setPubDate(rssItem.getPubDate());
        article.setDescriptionSize(Optional.ofNullable(rssItem.getDescription()).map(String::length).orElse(0));
        article.setGuid(rssItem.getGuid().getValue());
        return article;
    }
}
