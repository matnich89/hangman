package com.mat.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class WordGenerationUtil {

    private final static List<String> wordPool = new LinkedList<>();

    private final static  Random randomizer = new Random();

    static {
        wordPool.add("APPLE");
        wordPool.add("HAMSTER");
        wordPool.add("COMPUTER");
        wordPool.add("HOUSE");
        wordPool.add("GAMEDTO");
        wordPool.add("KEYBOARD");
    }


    public static String selectWord() {
        final int selection = randomizer.nextInt(wordPool.size());
        return wordPool.get(selection);
    }
}
