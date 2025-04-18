package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

public class ReducibleWordsFinder {

    private static final String WORD_LIST_URL = "https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt";
    private static final Set<String> dictionary = new HashSet<>();
    private static final Map<String, List<String>> pathMemo = new HashMap<>();


    public static void main(String[] args) throws Exception {

        loadDictionaryFromWeb();

        long start = System.currentTimeMillis();
        List<String> results = new ArrayList<>();

        for (String word : dictionary) {
            if (word.length() == 9) {
                List<String> path = getReductionPath(word);
                if (path != null) {
                    results.add(word);
                    System.out.println("Reduction path for " + word + ":");
                    for (String w : path) {
                        System.out.println("  -> " + w);
                    }
                    System.out.println();
                }
            }
        }

        for (String word : results) {
            System.out.println(word);
        }

        long end = System.currentTimeMillis();
        System.out.println("\nFound " + results.size() + " reducible 9-letter words in " + (end - start) + " ms");
    }
    private static void loadDictionaryFromWeb() throws Exception {
        URI uri = new URI(WORD_LIST_URL);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
            reader.lines()
                    .skip(2)
                    .map(String::trim)
                    .map(String::toUpperCase)
                    .forEach(dictionary::add);

            reader.close();
        }catch( IOException e){
            System.out.println("Failed to load dictionary from " + WORD_LIST_URL);
            System.out.println("Reason: " + e.getMessage());
        }catch (Exception e){
            System.out.println("Exception occurred while trying to load dictionary from " + WORD_LIST_URL);
            System.out.println("Reason: " + e.getMessage());
        }
    }


    private static List<String> getReductionPath(String word) {
        if (pathMemo.containsKey(word)) return pathMemo.get(word);
        if (!word.equals("A") && !word.equals("I") && !dictionary.contains(word)) return null;

        // A/I case
        if (word.equals("A") || word.equals("I")) {
            List<String> base = new ArrayList<>();
            base.add(word);
            pathMemo.put(word, base);
            return base;
        }

        for (int i = 0; i < word.length(); i++) {
            String shorter = word.substring(0, i) + word.substring(i + 1);
            List<String> subPath = getReductionPath(shorter);
            if (subPath != null) {
                List<String> fullPath = new ArrayList<>();
                fullPath.add(word);
                fullPath.addAll(subPath);
                pathMemo.put(word, fullPath);
                return fullPath;
            }
        }

        pathMemo.put(word, null);
        return null;
    }
}