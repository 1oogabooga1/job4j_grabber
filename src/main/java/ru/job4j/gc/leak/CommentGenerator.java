package ru.job4j.gc.leak;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CommentGenerator implements Generate {

    private static List<Comment> comments = new ArrayList<>();
    public static final int COUNT = 50;
    private static List<String> phrases;
    private UserGenerator userGenerator;
    private Random random;

    public CommentGenerator(Random random, UserGenerator userGenerator) {
        this.userGenerator = userGenerator;
        this.random = random;
        read();
    }

    private void read() {
        try {
            phrases = read("src/main/java/ru/job4j/gc/leak/files/phrases.txt");
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Comment> getComments() {
        return comments;
    }

    private Comment createComment(String text, User user) {
        return new Comment(text, user);
    }

    @Override
    public void generate() {
        comments.clear();
        String separator = System.lineSeparator();
        for (int i = 0; i < COUNT; i++) {
            String comment = String.format("%s%s%s%s%s",
                    phrases.get(random.nextInt(phrases.size())), separator,
                    phrases.get(random.nextInt(phrases.size())), separator,
                    phrases.get(random.nextInt(phrases.size())));
            comments.add(createComment(comment,
                    userGenerator.randomUser()));
        }
    }
}