package ru.job4j.grabber;

import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store {

    private Connection connection;

    public PsqlStore(Properties config) throws SQLException {
        try {
            Class.forName(config.getProperty("driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        connection = DriverManager.getConnection(config.getProperty("url"),
                config.getProperty("username"),
                config.getProperty("password"));
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement statement =
                     connection.prepareStatement("INSERT INTO post(name, text, link, created) VALUES (?,?,?,?) ON CONFLICT (link) DO NOTHING")) {
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getDescription());
            statement.setString(3, post.getLink());
            statement.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            statement.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> allPosts = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery("SELECT * FROM post");
            while (result.next()) {
                allPosts.add(new Post(result.getInt(1), result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getTimestamp(5).toLocalDateTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allPosts;
    }

    @Override
    public Post findById(int id) {
        List<Post> allPosts = getAll();
        for (Post post : allPosts) {
            if (post.getId() == id) {
                return post;
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws SQLException {
        Post post = new Post(1, "test", "Some description",
                "https://career.habr.com/companies/holdingt1",
                LocalDateTime.of(2011, 12, 03, 15, 30));
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PsqlStore store = new PsqlStore(config);
        store.save(post);
        System.out.println(store.findById(1));
        System.out.println(store.getAll());
    }
}