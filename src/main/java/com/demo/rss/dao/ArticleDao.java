package com.demo.rss.dao;

import com.demo.rss.dto.Article;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

public interface ArticleDao {
    void store(@NotNull List<Article> articles) throws SQLException;

    List<Article> loadArticles(int limit) throws SQLException;

    List<String> getNotStoredGUIDs(@NotNull List<String> guidsForCheck) throws SQLException;
}
