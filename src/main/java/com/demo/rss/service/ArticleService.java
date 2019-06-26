package com.demo.rss.service;

import com.demo.rss.dto.Article;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface ArticleService {
    int storeArticles(@NotNull List<Article> articles);

    List<Article> loadLastArticles(int limit);
}
