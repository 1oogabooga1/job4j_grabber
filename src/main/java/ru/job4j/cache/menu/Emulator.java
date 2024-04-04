package ru.job4j.cache.menu;

import ru.job4j.cache.DirFileCache;
import java.util.Scanner;

public class Emulator {
    public static final String MENU = """
                1. Указать кэшируемую директорию.
                2. Загрузить содержимое файла в кэш.
                3. Получить содержимое файла из кэша.
                4. Завершить программу.
            """;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        start(scanner);
    }

    private static void start(Scanner scanner) {
        String dir = null;
        DirFileCache cache = null;
        boolean run = true;
        while (run) {
            System.out.println(MENU);
            System.out.println("Выберите пункт меню");
            int userChoice = Integer.parseInt(scanner.nextLine());
            System.out.println(userChoice);
            if (1 == userChoice) {
                System.out.println("Введите название директории");
                dir = scanner.nextLine();
                cache = new DirFileCache(dir);
            } else if (2 == userChoice) {
                if (cache != null) {
                    System.out.println("Введите название файла");
                    String name = scanner.nextLine();
                    cache.get(name);
                } else {
                    System.out.println("Директория не указана.");
                }
            } else if (3 == userChoice) {
                if (cache != null) {
                    System.out.println("Введите название файла");
                    String name = scanner.nextLine();
                    System.out.println(cache.get(name));
                } else {
                    System.out.println("Директория не указана.");
                }
            } else if (4 == userChoice) {
                run = false;
                System.out.println("Конец работы");
            }
        }
    }
}
