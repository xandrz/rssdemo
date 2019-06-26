package com.demo.rss.service;

import com.demo.rss.dto.RSSItem;

import java.util.List;

public interface RSSLoadService {
    List<RSSItem> loadRSSItemsFromServer();

    void loadAndStoreItems();
}
