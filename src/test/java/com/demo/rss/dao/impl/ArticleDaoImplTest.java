package com.demo.rss.dao.impl;

import com.demo.rss.DemoRssApplication;
import com.demo.rss.dao.ArticleDao;
import com.demo.rss.dto.Article;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {DemoRssApplication.class },
        loader = SpringBootContextLoader.class)
@ActiveProfiles("test")
@Transactional

public class ArticleDaoImplTest {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private DataSource dataSource;

    @After
    public void clearArticles() throws SQLException {
        try(final Connection connection = dataSource.getConnection()) {
            try(final PreparedStatement stmt = connection.prepareStatement("delete from article")) {
                stmt.execute();
            }
        }
    }

    @Test
    public void testStoreArticles() throws SQLException {
        storeFirstArticle();
        assertEquals(1, countArticles());
    }
    
    @Test(expected = SQLException.class)
    public void testStoreArticlesWithDuplicatedGUIDIsNotAllowed() throws SQLException {
        storeFirstArticle();
        storeFirstArticle();
    }

    
    @Test
    public void testLoadArticles() throws SQLException {
        storeFirstArticle();
        final List<Article> articles = articleDao.loadArticles(2);
        assertEquals(1, articles.size());

    }

    @Test
    public void testLoadArticlesLimit() throws SQLException {
        storeFirstArticle();
        storeSecondArticle();
        final List<Article> articles = articleDao.loadArticles(1);
        assertEquals(1, articles.size());

    }

    @Test
    public void testGetNotStoredGUIDsGuidStored() throws SQLException {
        storeFirstArticle();
        final List<String> guids = new ArrayList<>();
        guids.add("guid");
        final List<String> notStoredGUIDs = articleDao.getNotStoredGUIDs(guids);
        assertEquals("guid is stored in DB, but isn't recognized as stored", 0, notStoredGUIDs.size());
    }

    @Test
    public void testGetNotStoredGUIDsGuidNotStored() throws SQLException {
        storeFirstArticle();
        final List<String> guids = new ArrayList<>();
        guids.add("guid_not_exist");
        final List<String> notStoredGUIDs = articleDao.getNotStoredGUIDs(guids);
        assertEquals("guid isn't stored in DB, but recognized as stored", 1, notStoredGUIDs.size());
    }

    private void storeFirstArticle() throws SQLException {
        final List<Article> articles = new ArrayList<>();
        final Article article = createArticle("guid", "title", 10, "link");
        articles.add(article);
        articleDao.store(articles);
    }

    private void storeSecondArticle() throws SQLException {
        final List<Article> articles = new ArrayList<>();
        final Article article = createArticle("guid2", "title2", 20, "link2");
        articles.add(article);
        articleDao.store(articles);
    }

    private Article createArticle(String guid, String title, int size, String link) {
        final Article article = new Article();
        article.setGuid(guid);
        article.setTitle(title);
        article.setDescriptionSize(size);
        article.setLink(link);
        return article;
    }

    private int countArticles() throws SQLException {
        try(final Connection connection = dataSource.getConnection()) {
            try(final PreparedStatement stmt = connection.prepareStatement("select count(1) from article")) {
                try(final ResultSet rs = stmt.executeQuery()) {
                    if(rs.next()) {
                        return rs.getInt(1);
                    } else {
                        return 0;
                    }
                }
            }
        }
    }
}