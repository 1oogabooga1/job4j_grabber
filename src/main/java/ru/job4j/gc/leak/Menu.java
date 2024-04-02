package ru.job4j.gc.leak;

import java.util.Random;
import java.util.Scanner;

public class Menu {

    public static final String MENU = """
                Введите 1 для создание поста.
                Введите 2, чтобы создать определенное количество постов.
                Введите 3, чтобы показать все посты.
                Введите 4, чтобы удалить все посты.
                Введите любое другое число для выхода.
            """;

    public static void main(String[] args) {
        Random random = new Random();
        UserGenerator userGenerator = new UserGenerator(random);
        CommentGenerator commentGenerator = new CommentGenerator(random, userGenerator);
        Scanner scanner = new Scanner(System.in);
        PostStore postStore = new PostStore();
        start(commentGenerator, scanner, userGenerator, postStore);
    }

    private static void start(CommentGenerator commentGenerator, Scanner scanner, UserGenerator userGenerator, PostStore postStore) {
        boolean run = true;
        while (run) {
            System.out.println(MENU);
            System.out.println("Выберите меню");
            int userChoice = Integer.parseInt(scanner.nextLine());
            System.out.println(userChoice);
            if (1 == userChoice) {
                System.out.println("Введите текст");
                String text = scanner.nextLine();
                userGenerator.generate();
                commentGenerator.generate();
                postStore.add(new Post(text, CommentGenerator.getComments()));
            } else if (2 == userChoice) {
                System.out.println("Введите текст");
                String text = scanner.nextLine();
                System.out.println("Выберите количество создаваемых постов");
                String count = scanner.nextLine();
                for (int i = 0; i < Integer.parseInt(count); i++) {
                    createPost(commentGenerator, userGenerator, postStore, text);
                }
            } else if (3 == userChoice) {
                System.out.println(PostStore.getPosts());
            } else if (4 == userChoice) {
                System.out.println("Все посты удалены");
                postStore.removeAll();
            } else {
                run = false;
                System.out.println("Конец работы");
            }
        }
    }

    private static void createPost(CommentGenerator commentGenerator,
                                   UserGenerator userGenerator, PostStore postStore, String text) {
        userGenerator.generate();
        commentGenerator.generate();
        postStore.add(new Post(text, CommentGenerator.getComments()));
    }
}
