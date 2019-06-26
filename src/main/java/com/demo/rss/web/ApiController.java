package com.demo.rss.web;

import com.demo.rss.dto.Article;
import com.demo.rss.service.ArticleService;
import com.demo.rss.service.RSSLoadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Controller
public class ApiController {
    final private ArticleService service;

    @Autowired
    public ApiController(ArticleService service) {
        this.service = service;
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET}, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<Article>> loadLastArticles(@RequestParam(name = "limit", defaultValue = "10") int limit) {
        final List<Article> articles = service.loadLastArticles(limit);
        return ResponseEntity.ok(articles);
    }
}
