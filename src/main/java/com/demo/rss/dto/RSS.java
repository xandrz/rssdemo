package com.demo.rss.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "rss")
public class RSS {
    @JacksonXmlProperty(localName = "channel")
    private RSSChanel rssChannel;

    public RSSChanel getRssChannel() {
        return rssChannel;
    }

    public void setRssChannel(RSSChanel rssChannel) {
        this.rssChannel = rssChannel;
    }
}
