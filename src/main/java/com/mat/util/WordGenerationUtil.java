package com.mat.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordGenerationUtil {

    private static List<String> wordPool = new LinkedList<>();

    private final static String WORDS_FILE = "src/main/resources/words.txt";

    private final static Random randomizer = new Random();

    static {
        try (Stream<String> stream = Files.lines(Paths.get(WORDS_FILE))) {
            wordPool = stream
                    .map(String::toUpperCase)
                    .collect(Collectors.toList());

            System.out.println("gdfgdf");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String selectWord() {
        final int selection = randomizer.nextInt(wordPool.size());
        return wordPool.get(selection);
    }
}
