package com.demo.rss.service.impl;

import com.demo.rss.DemoRssApplication;
import com.demo.rss.dao.ArticleDao;
import com.demo.rss.dto.RSSItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(
        classes = {DemoRssApplication.class},
        loader = SpringBootContextLoader.class)
@ActiveProfiles("test")
public class RSSLoadServiceImplTest {
    @Autowired
    private RSSLoadServiceImpl rssLoadService;

    @Autowired
    private ArticleDao articleDao;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(rssLoadService, "rssURL", getFileURL());
    }

    @Test
    public void testLoadRSSItemsFromServerFromLocalFile() {
        final List<RSSItem> rssItems = rssLoadService.loadRSSItemsFromServer();
        assertEquals(3, rssItems.size());
    }

    @Test
    public void testLoadRSSItemsFromServerFromNotExistFile() {
        ReflectionTestUtils.setField(rssLoadService, "rssURL", "NOT_EXIST");
        final List<RSSItem> rssItems = rssLoadService.loadRSSItemsFromServer();
        assertEquals(0, rssItems.size());
    }

    @Test
    public void testLoadAndStoreItems() throws SQLException {
        rssLoadService.loadAndStoreItems();
        final int articlesCount = articleDao.loadArticles(10).size();
        assertEquals(3, articlesCount);
    }

    private String getFileURL() {
        return "file://"+Paths.get("src/test/resources/testFiles/test-rss.xml").toAbsolutePath().toString();
    }
}