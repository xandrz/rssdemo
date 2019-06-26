package com.demo.rss.service;

import com.demo.rss.dto.Article;
import com.demo.rss.dto.RSSItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapperTest {

    @Test
    public void checkSizeCalculation() {
        final RSSItem rssItem = getRssItem();
        rssItem.setDescription("123456");
        final Article article = Mapper.mapRSSItem2Article(rssItem);
        assertEquals(6, article.getDescriptionSize());
    }

    @Test
    public void checkNullDescriptionHasSizeZero() {
        final RSSItem rssItem = getRssItem();
        rssItem.setDescription(null);
        final Article article = Mapper.mapRSSItem2Article(rssItem);
        assertEquals(0, article.getDescriptionSize());
    }

    private RSSItem getRssItem() {
        final RSSItem rssItem = new RSSItem();
        final RSSItem.GUID guid = new RSSItem.GUID();
        guid.setValue("guid");
        rssItem.setGuid(guid);
        return rssItem;
    }

}