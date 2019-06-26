package com.demo.rss.service.impl;

import com.demo.rss.dao.ArticleDao;
import com.demo.rss.dto.Article;
import com.demo.rss.exceptions.InternalException;
import com.demo.rss.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleDao articleDao;

    @Autowired
    public ArticleServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    @Transactional
    public int storeArticles(@NotNull List<Article> articles) {
        final List<String> actualGUIDs = articles.stream().map(Article::getGuid).collect(Collectors.toList());
        try {
            final List<String> notStoredGUIDs = articleDao.getNotStoredGUIDs(actualGUIDs);
            final List<Article> articlesToStore = articles.stream()
                    .filter(i -> notStoredGUIDs.contains(i.getGuid()))
                    .collect(Collectors.toList());
            if (!articlesToStore.isEmpty()) {
                articleDao.store(articlesToStore);
            }
            return articlesToStore.size();
        } catch (SQLException e) {
            logger.error("error storing to DB", e);
            return 0;
        }
    }

    @Override
    public List<Article> loadLastArticles(int limit) {
        try {
            return articleDao.loadArticles(limit);
        } catch (SQLException e) {
            logger.error("error while load articles from db", e);
            throw new InternalException(String.format("InternalServerError message: %s", e.getMessage()));
        }
    }
}
