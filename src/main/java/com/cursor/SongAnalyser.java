package com.cursor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SongAnalyser {
    private Scanner scanner;
    private static final Logger LOGGER = LogManager.getLogger(SongAnalyser.class);
    private List<String> words;
    private static final int WORD_SIZE = 3;
    private static final int NUMBER_OF_REPEAT = 10;



    public SongAnalyser(String fileName) {
        try {
            File file = new File(fileName);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            LOGGER.info("File not found");
        }
    }

    private List<String> getWordsFromFile() {
        List<String> strings = new ArrayList<>();
        scanner.useDelimiter("\\p{Space}");
        while (scanner.hasNext()) {
            String value = scanner.next();
            value = value.replaceAll("[,.\"()?!]", "");
            if (!value.isBlank()) {
                strings.add(value.toLowerCase());
            }
        }
        scanner.close();
        return strings;
    }

    public final void show() {
        words = getWordsFromFile();
        LOGGER.info("Amount of words: " + words.size());
        List<String> removedWords = removeSpecificWords();
        LOGGER.info("Amount of removed words: " + removedWords.size());
        Map<String, Integer> repeatingWords = getRepeatingWords();
        LOGGER.info("Most repeating words: " + repeatingWords.size());
        repeatingWords
                .forEach((key, value) -> LOGGER
                        .info(key + " - " + value + " times"));
    }

    private List<String> removeSpecificWords() {
        try {
            File file = new File("swearWords.txt");
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            LOGGER.info("File not found");
        }
        List<String> swearWords = getWordsFromFile();
        List<String> removedWords = new ArrayList<>();
        for (int i = 0; i < words.size(); i++) {
            String word = words.get(i).toLowerCase();
            if ((swearWords.contains(word) || (word.length() < WORD_SIZE))) {
                removedWords.add(word);
                words.remove(i);
                i--;
            }
        }
        return removedWords;
    }

    private Map<String, Integer> getRepeatingWords() {
        Map<String, Integer> repeatingWords = new LinkedHashMap<>();
        int n = 0;
        for (int i = 0; i < words.size() - 1; i++) {
            String word = words.get(i);
            for (String s : words) {
                if (word.equals(s)) {
                    n++;
                    repeatingWords.put(word, n);
                }
            }
            n = 0;
        }
        repeatingWords = repeatingWords
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() > NUMBER_OF_REPEAT)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()),
                        Map::putAll);
        return repeatingWords;
    }

}
