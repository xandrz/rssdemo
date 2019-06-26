package com.demo.rss.dao.impl;

import com.demo.rss.dao.ArticleDao;
import com.demo.rss.dto.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleDaoImpl implements ArticleDao {
    private final DataSource dataSource;

    @Autowired
    public ArticleDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void store(@NotNull List<Article> articles) throws SQLException {
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement("insert into article (guid, link, pub_date, author, title, description_size) values(?,?,?,?,?,?)")) {
                for (Article article : articles) {
                    stmt.setString(1, article.getGuid());
                    stmt.setString(2, article.getLink());
                    if (article.getPubDate() != null) {
                        stmt.setTimestamp(3, new Timestamp(article.getPubDate().getTime()));
                    } else {
                        stmt.setNull(3, Types.TIMESTAMP);
                    }
                    if (article.getAuthor() != null) {
                        stmt.setString(4, article.getAuthor());
                    } else {
                        stmt.setNull(4, Types.VARCHAR);
                    }
                    if (article.getTitle() != null) {
                        stmt.setString(5, article.getTitle());
                    } else {
                        stmt.setNull(5, Types.VARCHAR);
                    }
                    stmt.setInt(6, article.getDescriptionSize());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    @Override
    public List<Article> loadArticles(int limit) throws SQLException {
        final List<Article> articles = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement("select id, guid, link, pub_date, author, title, description_size from article order by id desc limit ?")) {
                stmt.setInt(1, limit);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        final Article article = new Article();
                        article.setId(rs.getInt("id"));
                        article.setGuid(rs.getString("guid"));
                        article.setLink(rs.getString("link"));
                        article.setPubDate(rs.getTimestamp("pub_date"));
                        article.setAuthor(rs.getString("author"));
                        article.setTitle(rs.getString("title"));
                        article.setDescriptionSize(rs.getInt("description_size"));
                        articles.add(article);
                    }
                }
            }
        }
        return articles;
    }

    public List<String> getNotStoredGUIDs(@NotNull List<String> guidsForCheck) throws SQLException {
        final ArrayList<String> guids = new ArrayList<>(guidsForCheck);
        final ArrayList<String> storedGUIDs = new ArrayList<>();
        try (Connection con = dataSource.getConnection()) {
            final String sql = getNotStoredGUIDsSQL(guids);
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        storedGUIDs.add(rs.getString("guid"));
                    }
                }
            }
        }
        guids.removeAll(storedGUIDs);
        return guids;
    }

    private String getNotStoredGUIDsSQL(@NotNull List<String> guids) {
        final String inStatement = guids.stream().collect(Collectors.joining("', '", "'", "'"));
        final StringBuffer sql = new StringBuffer();
        sql.append("select guid from article where guid in (").append(inStatement).append(")");
        return sql.toString();
    }
}
