package ru.job4j.grabber;

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

    private Post createPost(int id, String title, String link, String description, LocalDateTime created) {
        return new Post(id, title, description, link, created);
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
                allPosts.add(createPost(result.getInt(1), result.getString(2),
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
        try (Statement statement = connection.createStatement()) {
            ResultSet rsl = statement.executeQuery(String.format("SELECT * FROM post WHERE id = %s", id));
            return createPost(rsl.getInt(1),
                    rsl.getString(2),
                    rsl.getString(3),
                    rsl.getString(4),
                    rsl.getTimestamp(5).toLocalDateTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}