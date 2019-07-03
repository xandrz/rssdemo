package com.demo.rss.service.impl;

import com.demo.rss.dto.Article;
import com.demo.rss.dto.RSS;
import com.demo.rss.dto.RSSChanel;
import com.demo.rss.dto.RSSItem;
import com.demo.rss.service.ArticleService;
import com.demo.rss.utils.mapping.Mapper;
import com.demo.rss.service.RSSLoadService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RSSLoadServiceImpl implements RSSLoadService {
    private static final Logger logger = LoggerFactory.getLogger(RSSLoadServiceImpl.class);

    private final XMLInputFactory xmlInputFactory = XMLInputFactory.newFactory();
    private final XmlMapper mapper = new XmlMapper();

    private final ArticleService articleService;

    @Value("${rss.url}")
    private String rssURL;

    @Autowired
    public RSSLoadServiceImpl(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostConstruct
    private void init() {
        configureMapper();
    }

    private void configureMapper() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("EEE, dd MMM YYYY HH:mm:ss Z", Locale.ENGLISH));
    }

    @Override
    public List<RSSItem> loadRSSItemsFromServer() {
        logger.debug("loadRSSItemsFromServer from {} started", rssURL);
        try {
            RSS rss = loadRSS();
            return Optional.ofNullable(rss.getRssChannel()).map(RSSChanel::getItems).orElse(Collections.emptyList());
        } catch (IOException | XMLStreamException e) {
            logger.error("error while load rss", e);
        } finally {
            logger.debug("loadRSSItemsFromServer finished");
        }
        return Collections.emptyList();
    }

    private RSS loadRSS() throws IOException, XMLStreamException {
        URL url = new URL(rssURL);
        try (InputStream inputStream = url.openStream()) {
            final XMLStreamReader reader = xmlInputFactory.createXMLStreamReader(inputStream);
            return mapper.readValue(reader, RSS.class);
        }
    }


    @Scheduled(fixedDelayString = "${rss.reload.delay}", initialDelayString = "${rss.initial.delay:0}")
    public void loadAndStoreItems() {
        logger.debug("start LoadAndStore process...");
        final List<RSSItem> rssItems = loadRSSItemsFromServer();
        int count = 0;
        if (!rssItems.isEmpty()) {
            final List<Article> articles = rssItems.stream().map(Mapper::mapRSSItem2Article).collect(Collectors.toList());
            count = articleService.storeArticles(articles);
        }
        logger.debug("finished LoadAndStore process stored {} articles", count);
    }
}