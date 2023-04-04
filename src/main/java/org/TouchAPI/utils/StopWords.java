package org.TouchAPI.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class StopWords {

    private static Set<String> stopWords;

    static {
        try {
            loadStopWords("stopwords.txt");
        } catch (IOException e) {
            throw new RuntimeException("Error loading stop words.", e);
        }
    }

    public static boolean isStopWord(String word) {
        return stopWords.contains(word.toLowerCase());
    }

    public static Set<String> loadStopWords(String fileName) throws IOException {
        if (stopWords == null) {
            stopWords = new HashSet<>();
            InputStream inputStream = StopWords.class.getClassLoader().getResourceAsStream(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                stopWords.add(line.trim().toLowerCase());
            }
            reader.close();
        }
        return stopWords;
    }

}
