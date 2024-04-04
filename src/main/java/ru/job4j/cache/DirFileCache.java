package ru.job4j.cache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.stream.Collectors;

public class DirFileCache extends AbstractCache<String, String> {

    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    @Override
    protected String load(String key) {
        String content = "";
        try (BufferedReader in = new BufferedReader(new FileReader(String.format("%s/%s", cachingDir, key)))) {
            content = in.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content;
    }
}