package com.demo.rss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.demo.rss.service", "com.demo.rss.dao", "com.demo.rss.web"})
@EnableScheduling
public class DemoRssApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoRssApplication.class, args);
    }
}
